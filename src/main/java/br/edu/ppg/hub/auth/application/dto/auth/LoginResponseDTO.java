package br.edu.ppg.hub.auth.application.dto.auth;

import br.edu.ppg.hub.auth.application.dto.usuario.UsuarioResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login bem-sucedido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de login bem-sucedido")
public class LoginResponseDTO {

    /**
     * Access token JWT.
     */
    @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * Refresh token JWT (para renovar access token).
     */
    @Schema(description = "Token de renovação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    /**
     * Tipo do token (sempre "Bearer").
     */
    @Builder.Default
    @Schema(description = "Tipo do token", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * Tempo de expiração do access token em segundos.
     */
    @Schema(description = "Tempo de expiração em segundos", example = "900")
    private Long expiresIn;

    /**
     * Dados do usuário autenticado.
     */
    @Schema(description = "Dados do usuário autenticado")
    private UsuarioResponseDTO usuario;
}
