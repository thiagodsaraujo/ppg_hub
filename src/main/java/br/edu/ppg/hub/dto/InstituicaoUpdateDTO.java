package br.edu.ppg.hub.dto;

import br.edu.ppg.hub.validation.ValidCNPJ;
import br.edu.ppg.hub.validation.ValidCodigo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para atualização de instituição.
 *
 * Todos os campos são opcionais para permitir atualização parcial.
 * Apenas campos fornecidos serão atualizados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para atualização de uma instituição (todos os campos são opcionais)")
public class InstituicaoUpdateDTO {

    @Schema(description = "Código único da instituição", example = "UEPB")
    @Size(min = 2, max = 20, message = "Código deve ter entre 2 e 20 caracteres")
    @ValidCodigo
    private String codigo;

    @Schema(description = "Nome oficial completo da instituição",
            example = "Universidade Estadual da Paraíba")
    @Size(min = 5, max = 500, message = "Nome completo deve ter entre 5 e 500 caracteres")
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    @Schema(description = "Nome abreviado para exibição", example = "UEPB")
    @Size(min = 2, max = 50, message = "Nome abreviado deve ter entre 2 e 50 caracteres")
    @JsonProperty("nome_abreviado")
    private String nomeAbreviado;

    @Schema(description = "Sigla oficial da instituição", example = "UEPB")
    @Size(min = 2, max = 10, message = "Sigla deve ter entre 2 e 10 caracteres")
    private String sigla;

    @Schema(description = "Tipo da instituição", example = "Estadual",
            allowableValues = {"Federal", "Estadual", "Municipal", "Privada"})
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
    private Boolean ativo;

    @Schema(description = "Configurações específicas da instituição")
    private Map<String, Object> configuracoes;
}
