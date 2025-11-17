package com.ppghub.config;

import com.ppghub.domain.exception.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor que aplica rate limiting baseado em IP usando Bucket4j.
 * Implementa o algoritmo Token Bucket com armazenamento distribuído em Redis.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimitConfig rateLimitConfig;
    private final LettuceBasedProxyManager<String> proxyManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = getClientIP(request);
        String bucketKey = "rate_limit:" + ip;

        log.debug("Verificando rate limit para IP: {}", ip);

        Bucket bucket = rateLimitConfig.resolveBucket(bucketKey, proxyManager);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Adicionar headers informativos sobre rate limit
            response.addHeader("X-Rate-Limit-Limit", String.valueOf(rateLimitConfig.getRequestsPerMinute()));
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.addHeader("X-Rate-Limit-Reset", String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            return true;
        }

        // Rate limit excedido
        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000; // Converter para segundos
        log.warn("Rate limit excedido para IP: {}. Aguardar {} segundos", ip, waitForRefill);

        throw new RateLimitExceededException(
            "Taxa de requisições excedida. Limite: " + rateLimitConfig.getRequestsPerMinute() + " requisições por minuto",
            waitForRefill
        );
    }

    /**
     * Extrai o IP real do cliente, considerando proxies e load balancers.
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        // X-Forwarded-For pode conter múltiplos IPs, o primeiro é o cliente original
        return xfHeader.split(",")[0].trim();
    }
}
