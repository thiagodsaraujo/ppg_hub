package com.ppghub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing).
 * Permite que aplicações frontend em diferentes domínios acessem a API.
 *
 * Esta configuração é essencial para a integração com frontend web.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

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
}
