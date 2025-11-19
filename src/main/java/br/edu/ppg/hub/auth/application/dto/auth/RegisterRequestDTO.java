package br.edu.ppg.hub.auth.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de registro de novo usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição de registro de novo usuário")
public class RegisterRequestDTO {

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 255, message = "Nome completo deve ter entre 3 e 255 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva Santos")
    private String nomeCompleto;

    /**
     * Email do usuário (será usado para login).
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "joao.silva@example.com")
    private String email;

    /**
     * Senha do usuário.
     * Deve ter no mínimo 8 caracteres, incluindo letra maiúscula, minúscula e número.
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número"
    )
    @Schema(description = "Senha do usuário", example = "SenhaSegura123!")
    private String password;

    /**
     * CPF do usuário (obrigatório para brasileiros).
     */
    @Pattern(
            regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$",
            message = "CPF deve estar no formato XXX.XXX.XXX-XX ou XXXXXXXXXXX"
    )
    @Schema(description = "CPF do usuário", example = "123.456.789-00")
    private String cpf;

    /**
     * Telefone de contato.
     */
    @Pattern(
            regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$|^$",
            message = "Telefone deve estar no formato (XX) XXXXX-XXXX"
    )
    @Schema(description = "Telefone de contato", example = "(83) 98765-4321")
    private String telefone;

    /**
     * Passaporte (obrigatório para estrangeiros que não têm CPF).
     */
    @Schema(description = "Número do passaporte", example = "AB123456")
    private String passaporte;
}
