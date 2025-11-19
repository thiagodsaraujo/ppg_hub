package br.edu.ppg.hub.core.application.dto.programa;

import br.edu.ppg.hub.core.domain.enums.ModalidadePrograma;
import br.edu.ppg.hub.core.domain.enums.NivelPrograma;
import br.edu.ppg.hub.core.domain.enums.StatusPrograma;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de um novo Programa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaCreateDTO {

    @NotNull(message = "ID da instituição é obrigatório")
    private Long instituicaoId;

    @Size(max = 20, message = "Código CAPES deve ter no máximo 20 caracteres")
    private String codigoCapes;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    @NotBlank(message = "Sigla é obrigatória")
    @Size(max = 20, message = "Sigla deve ter no máximo 20 caracteres")
    private String sigla;

    @Size(max = 255, message = "Área de concentração deve ter no máximo 255 caracteres")
    private String areaConcentracao;

    @NotNull(message = "Nível é obrigatório")
    private NivelPrograma nivel;

    private ModalidadePrograma modalidade = ModalidadePrograma.PRESENCIAL;

    private LocalDate inicioFuncionamento;

    @Min(value = 1, message = "Conceito CAPES deve ser no mínimo 1")
    @Max(value = 7, message = "Conceito CAPES deve ser no máximo 7")
    private Integer conceitoCapes;

    private LocalDate dataUltimaAvaliacao;

    @Size(max = 20, message = "Triênio deve ter no máximo 20 caracteres")
    private String trienioAvaliacao;

    private Long coordenadorId;

    private Long coordenadorAdjuntoId;

    private LocalDate mandatoInicio;

    private LocalDate mandatoFim;

    @Min(value = 0, message = "Créditos mínimos mestrado deve ser no mínimo 0")
    private Integer creditosMinimosMestrado = 24;

    @Min(value = 0, message = "Créditos mínimos doutorado deve ser no mínimo 0")
    private Integer creditosMinimosDoutorado = 48;

    @Min(value = 1, message = "Prazo máximo mestrado deve ser no mínimo 1 mês")
    private Integer prazoMaximoMestrado = 24;

    @Min(value = 1, message = "Prazo máximo doutorado deve ser no mínimo 1 mês")
    private Integer prazoMaximoDoutorado = 48;

    private String openalexInstitutionId;

    private StatusPrograma status = StatusPrograma.ATIVO;

    private String configuracoes;
}
