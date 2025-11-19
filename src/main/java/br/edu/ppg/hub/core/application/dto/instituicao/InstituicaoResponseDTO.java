package br.edu.ppg.hub.core.application.dto.instituicao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para resposta de consulta de instituição.
 *
 * Contém todos os dados da instituição, incluindo campos automáticos
 * como ID e timestamps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados completos de uma instituição")
public class InstituicaoResponseDTO {

    @Schema(description = "ID único da instituição", example = "1")
    private Long id;

    @Schema(description = "Código único da instituição", example = "UEPB")
    private String codigo;

    @Schema(description = "Nome oficial completo da instituição",
            example = "Universidade Estadual da Paraíba")
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    @Schema(description = "Nome abreviado para exibição", example = "UEPB")
    @JsonProperty("nome_abreviado")
    private String nomeAbreviado;

    @Schema(description = "Sigla oficial da instituição", example = "UEPB")
    private String sigla;

    @Schema(description = "Tipo da instituição", example = "Estadual")
    private String tipo;

    @Schema(description = "CNPJ da instituição", example = "12.345.678/0001-90")
    private String cnpj;

    @Schema(description = "Natureza jurídica detalhada", example = "Autarquia Estadual")
    @JsonProperty("natureza_juridica")
    private String naturezaJuridica;

    @Schema(description = "Dados de endereço em formato JSON")
    private Map<String, Object> endereco;

    @Schema(description = "Telefones e emails em formato JSON")
    private Map<String, Object> contatos;

    @Schema(description = "URLs das redes sociais")
    @JsonProperty("redes_sociais")
    private Map<String, Object> redesSociais;

    @Schema(description = "URL do logotipo da instituição",
            example = "https://uepb.edu.br/assets/logo.png")
    @JsonProperty("logo_url")
    private String logoUrl;

    @Schema(description = "Site oficial da instituição", example = "https://uepb.edu.br")
    private String website;

    @Schema(description = "Data de fundação da instituição", example = "1987-04-05T00:00:00")
    private LocalDateTime fundacao;

    @Schema(description = "ID da instituição na base OpenAlex", example = "I123456789")
    @JsonProperty("openalex_institution_id")
    private String openalexInstitutionId;

    @Schema(description = "Research Organization Registry ID", example = "https://ror.org/01234567")
    @JsonProperty("ror_id")
    private String rorId;

    @Schema(description = "Se a instituição está ativa no sistema", example = "true")
    private Boolean ativo;

    @Schema(description = "Configurações específicas da instituição")
    private Map<String, Object> configuracoes;

    @Schema(description = "Data de criação do registro", example = "2024-01-15T10:30:00")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2024-01-15T10:30:00")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "Endereço formatado completo",
            example = "Rua Baraúnas, 351 - Campina Grande - PB")
    @JsonProperty("endereco_completo")
    private String enderecoCompleto;

    @Schema(description = "Email ou telefone principal", example = "contato@uepb.edu.br")
    @JsonProperty("contato_principal")
    private String contatoPrincipal;
}
