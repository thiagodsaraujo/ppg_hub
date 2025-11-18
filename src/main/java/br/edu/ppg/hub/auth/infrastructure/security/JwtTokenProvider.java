package br.edu.ppg.hub.auth.infrastructure.security;

import br.edu.ppg.hub.shared.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provedor de tokens JWT.
 *
 * Responsável por:
 * - Gerar access tokens
 * - Gerar refresh tokens
 * - Validar tokens
 * - Extrair informações dos tokens
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    /**
     * Gera um access token JWT para o usuário.
     *
     * @param userDetails Detalhes do usuário
     * @return Access token JWT
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Adicionar roles/authorities ao token
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);

        return generateToken(claims, userDetails.getUsername(), jwtConfig.getExpiration());
    }

    /**
     * Gera um refresh token JWT para o usuário.
     *
     * @param userDetails Detalhes do usuário
     * @return Refresh token JWT
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return generateToken(claims, userDetails.getUsername(), jwtConfig.getRefreshExpiration());
    }

    /**
     * Gera um token JWT com claims customizados.
     *
     * @param extraClaims Claims adicionais
     * @param subject Subject (username/email)
     * @param expiration Tempo de expiração em milissegundos
     * @return Token JWT
     */
    private String generateToken(Map<String, Object> extraClaims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(jwtConfig.getIssuer())
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Valida um token JWT.
     *
     * @param token Token a validar
     * @param userDetails Detalhes do usuário
     * @return true se o token é válido
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Erro ao validar token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida apenas a estrutura do token (sem verificar usuário).
     *
     * @param token Token a validar
     * @return true se o token é válido
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("Assinatura JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims JWT vazias: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrai o username (email) do token.
     *
     * @param token Token JWT
     * @return Username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extrai a data de expiração do token.
     *
     * @param token Token JWT
     * @return Data de expiração
     */
    public Date getExpirationFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extrai um claim específico do token.
     *
     * @param token Token JWT
     * @param claimsResolver Função para extrair o claim
     * @return Valor do claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai todos os claims do token.
     *
     * @param token Token JWT
     * @return Claims
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica se o token está expirado.
     *
     * @param token Token JWT
     * @return true se expirado
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Obtém a chave de assinatura a partir do secret configurado.
     *
     * @return Chave de assinatura
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Calcula o tempo restante até a expiração do token em segundos.
     *
     * @param token Token JWT
     * @return Segundos até a expiração
     */
    public Long getExpirationInSeconds(String token) {
        Date expiration = getExpirationFromToken(token);
        long now = System.currentTimeMillis();
        long expirationTime = expiration.getTime();
        return (expirationTime - now) / 1000;
    }

    /**
     * Retorna o tempo de expiração padrão do access token em segundos.
     *
     * @return Segundos de expiração
     */
    public Long getAccessTokenExpirationInSeconds() {
        return jwtConfig.getExpiration() / 1000;
    }
}
