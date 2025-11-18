package br.edu.ppg.hub.auth.application.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para resposta com dados do usuário.
 * Não inclui informações sensíveis como senha.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados do usuário (resposta)")
public class UsuarioResponseDTO {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "UUID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID uuid;

    @Schema(description = "Nome completo", example = "João Silva Santos")
    private String nomeCompleto;

    @Schema(description = "Nome preferido/social", example = "João Silva")
    private String nomePreferido;

    @Schema(description = "Email principal", example = "joao.silva@example.com")
    private String email;

    @Schema(description = "Email alternativo", example = "joao.alternativo@example.com")
    private String emailAlternativo;

    @Schema(description = "Telefone de contato", example = "(83) 98765-4321")
    private String telefone;

    @Schema(description = "CPF", example = "123.456.789-00")
    private String cpf;

    @Schema(description = "RG", example = "1234567")
    private String rg;

    @Schema(description = "Passaporte", example = "AB123456")
    private String passaporte;

    @Schema(description = "Email verificado", example = "true")
    private Boolean emailVerificado;

    @Schema(description = "Data de verificação do email")
    private LocalDateTime emailVerificadoEm;

    @Schema(description = "Data de nascimento", example = "1990-01-15")
    private LocalDate dataNascimento;

    @Schema(description = "Gênero", example = "Masculino")
    private String genero;

    @Schema(description = "Nacionalidade", example = "Brasileira")
    private String nacionalidade;

    @Schema(description = "Naturalidade", example = "Campina Grande - PB")
    private String naturalidade;

    @Schema(description = "Endereço em formato JSON")
    private String endereco;

    @Schema(description = "ORCID", example = "0000-0002-1825-0097")
    private String orcid;

    @Schema(description = "ID do currículo Lattes", example = "1234567890123456")
    private String lattesId;

    @Schema(description = "ID do Google Scholar", example = "abcdefghijk")
    private String googleScholarId;

    @Schema(description = "ID do ResearchGate", example = "John_Doe")
    private String researchgateId;

    @Schema(description = "Perfil do LinkedIn", example = "joao-silva")
    private String linkedin;

    @Schema(description = "ID do autor no OpenAlex")
    private String openalexAuthorId;

    @Schema(description = "Data da última sincronização com OpenAlex")
    private LocalDateTime ultimoSyncOpenalex;

    @Schema(description = "Configurações do usuário")
    private String configuracoes;

    @Schema(description = "Preferências do usuário")
    private String preferencias;

    @Schema(description = "URL do avatar")
    private String avatarUrl;

    @Schema(description = "Biografia do usuário")
    private String biografia;

    @Schema(description = "Data do último login")
    private LocalDateTime ultimoLogin;

    @Schema(description = "Conta está bloqueada", example = "false")
    private Boolean contaBloqueada;

    @Schema(description = "Bloqueada até")
    private LocalDateTime bloqueadaAte;

    @Schema(description = "Usuário está ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Data de criação")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    private LocalDateTime updatedAt;

    @Schema(description = "Roles do usuário")
    private List<String> roles;

    /**
     * Retorna o nome a ser exibido (preferido ou completo).
     */
    public String getNomeExibicao() {
        return (nomePreferido != null && !nomePreferido.isBlank())
                ? nomePreferido
                : nomeCompleto;
    }
}
