package com.ppghub.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing) e Interceptors.
 * Permite que aplicações frontend em diferentes domínios acessem a API.
 * Também registra interceptors como o rate limiter.
 *
 * Esta configuração é essencial para a integração com frontend web.
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final RateLimiterInterceptor rateLimiterInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v1/**")
                .allowedOriginPatterns(
                    "http://localhost:*",      // Desenvolvimento local (qualquer porta)
                    "https://*.ppghub.com",     // Produção (todos os subdomínios)
                    "https://ppghub.com"        // Produção (domínio principal)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders(
                    "X-Total-Count",       // Total de registros (paginação)
                    "X-Page-Number",       // Número da página atual
                    "X-Page-Size",         // Tamanho da página
                    "X-Total-Pages"        // Total de páginas
                )
                .allowCredentials(true)    // Permite envio de cookies/credentials
                .maxAge(3600);            // Cache de preflight (1 hora)
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Aplicar rate limiting a todos os endpoints da API
        registry.addInterceptor(rateLimiterInterceptor)
                .addPathPatterns("/v1/**")
                .excludePathPatterns(
                    "/v1/swagger-ui/**",
                    "/v1/api-docs/**",
                    "/actuator/**"
                );
    }
}
