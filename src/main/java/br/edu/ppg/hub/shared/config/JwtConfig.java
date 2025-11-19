package br.edu.ppg.hub.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações JWT externalizadas via application.yml
 *
 * Exemplo de uso no application.yml:
 * <pre>
 * jwt:
 *   secret: your-secret-key-here
 *   expiration: 900000        # 15 minutos (em ms)
 *   refresh-expiration: 604800000  # 7 dias (em ms)
 *   issuer: ppg-hub
 * </pre>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * Chave secreta para assinar o JWT.
     * IMPORTANTE: Use uma chave forte em produção (mínimo 256 bits).
     * Em produção, obtenha de variável de ambiente.
     */
    private String secret = "ppg-hub-default-secret-key-change-in-production-for-security";

    /**
     * Tempo de expiração do access token em milissegundos.
     * Padrão: 15 minutos (900000 ms)
     */
    private Long expiration = 900000L; // 15 minutos

    /**
     * Tempo de expiração do refresh token em milissegundos.
     * Padrão: 7 dias (604800000 ms)
     */
    private Long refreshExpiration = 604800000L; // 7 dias

    /**
     * Emissor do token (issuer).
     * Padrão: ppg-hub
     */
    private String issuer = "ppg-hub";

    /**
     * Header HTTP onde o token é enviado.
     * Padrão: Authorization
     */
    private String header = "Authorization";

    /**
     * Prefixo do token no header.
     * Padrão: Bearer
     */
    private String prefix = "Bearer ";
}
