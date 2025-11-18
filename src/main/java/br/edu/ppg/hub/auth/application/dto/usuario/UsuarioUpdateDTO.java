package br.edu.ppg.hub.auth.application.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para atualização de usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para atualização de usuário")
public class UsuarioUpdateDTO {

    @Size(min = 3, max = 255, message = "Nome completo deve ter entre 3 e 255 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva Santos")
    private String nomeCompleto;

    @Size(max = 100, message = "Nome preferido deve ter no máximo 100 caracteres")
    @Schema(description = "Nome preferido/social", example = "João Silva")
    private String nomePreferido;

    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "joao.silva@example.com")
    private String email;

    @Email(message = "Email alternativo deve ser válido")
    @Schema(description = "Email alternativo", example = "joao.alternativo@example.com")
    private String emailAlternativo;

    @Pattern(
            regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$|^$",
            message = "Telefone deve estar no formato (XX) XXXXX-XXXX"
    )
    @Schema(description = "Telefone de contato", example = "(83) 98765-4321")
    private String telefone;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    @Schema(description = "RG do usuário", example = "1234567")
    private String rg;

    @Schema(description = "Data de nascimento", example = "1990-01-15")
    private LocalDate dataNascimento;

    @Size(max = 20, message = "Gênero deve ter no máximo 20 caracteres")
    @Schema(description = "Gênero", example = "Masculino")
    private String genero;

    @Size(max = 50, message = "Nacionalidade deve ter no máximo 50 caracteres")
    @Schema(description = "Nacionalidade", example = "Brasileira")
    private String nacionalidade;

    @Size(max = 100, message = "Naturalidade deve ter no máximo 100 caracteres")
    @Schema(description = "Naturalidade", example = "Campina Grande - PB")
    private String naturalidade;

    @Schema(description = "Endereço em formato JSON")
    private String endereco;

    @Size(max = 100, message = "ORCID deve ter no máximo 100 caracteres")
    @Schema(description = "ORCID", example = "0000-0002-1825-0097")
    private String orcid;

    @Size(max = 100, message = "ID do Lattes deve ter no máximo 100 caracteres")
    @Schema(description = "ID do currículo Lattes", example = "1234567890123456")
    private String lattesId;

    @Size(max = 100, message = "ID do Google Scholar deve ter no máximo 100 caracteres")
    @Schema(description = "ID do Google Scholar", example = "abcdefghijk")
    private String googleScholarId;

    @Size(max = 100, message = "ID do ResearchGate deve ter no máximo 100 caracteres")
    @Schema(description = "ID do ResearchGate", example = "John_Doe")
    private String researchgateId;

    @Size(max = 100, message = "LinkedIn deve ter no máximo 100 caracteres")
    @Schema(description = "Perfil do LinkedIn", example = "joao-silva")
    private String linkedin;

    @Schema(description = "URL do avatar")
    private String avatarUrl;

    @Schema(description = "Biografia do usuário")
    private String biografia;

    @Schema(description = "Configurações do usuário em formato JSON")
    private String configuracoes;

    @Schema(description = "Preferências do usuário em formato JSON")
    private String preferencias;
}
