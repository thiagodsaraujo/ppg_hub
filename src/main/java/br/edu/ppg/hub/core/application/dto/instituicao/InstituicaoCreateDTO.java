package br.edu.ppg.hub.core.application.dto.instituicao;

import br.edu.ppg.hub.shared.validation.ValidCNPJ;
import br.edu.ppg.hub.shared.validation.ValidCodigo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para criação de instituição.
 *
 * Contém todos os campos necessários para criar uma nova instituição,
 * com validações apropriadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criação de uma instituição")
public class InstituicaoCreateDTO {

    @Schema(description = "Código único da instituição", example = "UEPB", required = true)
    @NotBlank(message = "Código é obrigatório")
    @Size(min = 2, max = 20, message = "Código deve ter entre 2 e 20 caracteres")
    @ValidCodigo
    private String codigo;

    @Schema(description = "Nome oficial completo da instituição",
            example = "Universidade Estadual da Paraíba", required = true)
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 5, max = 500, message = "Nome completo deve ter entre 5 e 500 caracteres")
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    @Schema(description = "Nome abreviado para exibição", example = "UEPB", required = true)
    @NotBlank(message = "Nome abreviado é obrigatório")
    @Size(min = 2, max = 50, message = "Nome abreviado deve ter entre 2 e 50 caracteres")
    @JsonProperty("nome_abreviado")
    private String nomeAbreviado;

    @Schema(description = "Sigla oficial da instituição", example = "UEPB", required = true)
    @NotBlank(message = "Sigla é obrigatória")
    @Size(min = 2, max = 10, message = "Sigla deve ter entre 2 e 10 caracteres")
    private String sigla;

    @Schema(description = "Tipo da instituição", example = "Estadual", required = true,
            allowableValues = {"Federal", "Estadual", "Municipal", "Privada"})
    @NotBlank(message = "Tipo é obrigatório")
    @Pattern(regexp = "^(Federal|Estadual|Municipal|Privada)$",
             message = "Tipo deve ser: Federal, Estadual, Municipal ou Privada")
    private String tipo;

    @Schema(description = "CNPJ da instituição", example = "12.345.678/0001-90")
    @ValidCNPJ
    private String cnpj;

    @Schema(description = "Natureza jurídica detalhada", example = "Autarquia Estadual")
    @Size(max = 100, message = "Natureza jurídica deve ter no máximo 100 caracteres")
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
    @Size(max = 500, message = "URL do logo deve ter no máximo 500 caracteres")
    @JsonProperty("logo_url")
    private String logoUrl;

    @Schema(description = "Site oficial da instituição", example = "https://uepb.edu.br")
    @Size(max = 500, message = "Website deve ter no máximo 500 caracteres")
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
    @Builder.Default
    private Boolean ativo = true;

    @Schema(description = "Configurações específicas da instituição")
    @Builder.Default
    private Map<String, Object> configuracoes = Map.of();
}
