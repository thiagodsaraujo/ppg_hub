package br.edu.ppg.hub.auth.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de alteração de senha (usuário autenticado).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição de alteração de senha")
public class ChangePasswordDTO {

    /**
     * Senha atual.
     */
    @NotBlank(message = "Senha atual é obrigatória")
    @Schema(description = "Senha atual", example = "SenhaAtual123!")
    private String oldPassword;

    /**
     * Nova senha.
     */
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número"
    )
    @Schema(description = "Nova senha", example = "NovaSenhaSegura123!")
    private String newPassword;
}
