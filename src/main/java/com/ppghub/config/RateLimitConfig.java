package com.ppghub.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Configuração de Rate Limiting usando Bucket4j com Redis.
 * Limita requisições por IP para proteger a API de abuso.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RateLimitConfig {

    @Value("${rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    /**
     * Cria o RedisClient para uso com Bucket4j.
     */
    @Bean
    public RedisClient redisClient() {
        String redisUri = String.format("redis://%s:%d", redisHost, redisPort);
        log.info("Configurando RedisClient para Bucket4j: {}", redisUri);
        return RedisClient.create(redisUri);
    }

    /**
     * Cria a conexão Redis para Bucket4j.
     */
    @Bean
    public StatefulRedisConnection<String, byte[]> redisConnection(RedisClient redisClient) {
        RedisCodec<String, byte[]> codec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
        return redisClient.connect(codec);
    }

    /**
     * Cria o ProxyManager para gerenciar buckets no Redis.
     */
    @Bean
    public LettuceBasedProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> connection) {
        return LettuceBasedProxyManager.builderFor(connection)
                .build();
    }

    /**
     * Cria um bucket para um IP específico.
     *
     * @param key Identificador do bucket (geralmente IP do cliente)
     * @return Bucket configurado com os limites definidos
     */
    public Bucket resolveBucket(String key, LettuceBasedProxyManager<String> proxyManager) {
        Supplier<io.github.bucket4j.BucketConfiguration> configSupplier = getConfigSupplier();
        return proxyManager.builder().build(key, configSupplier);
    }

    /**
     * Fornece a configuração do bucket (limite de requisições).
     * Configurado para permitir N requisições por minuto com refill gradual.
     */
    private Supplier<io.github.bucket4j.BucketConfiguration> getConfigSupplier() {
        return () -> {
            // Refill gradual: recupera 1 token a cada (60/requestsPerMinute) segundos
            Refill refill = Refill.intervally(requestsPerMinute, Duration.ofMinutes(1));

            // Bandwidth: capacidade máxima e taxa de refill
            Bandwidth limit = Bandwidth.classic(requestsPerMinute, refill);

            return io.github.bucket4j.BucketConfiguration.builder()
                    .addLimit(limit)
                    .build();
        };
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }
}
