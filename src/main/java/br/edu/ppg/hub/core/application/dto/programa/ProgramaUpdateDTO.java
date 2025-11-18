package br.edu.ppg.hub.core.application.dto.programa;

import br.edu.ppg.hub.core.domain.enums.ModalidadePrograma;
import br.edu.ppg.hub.core.domain.enums.NivelPrograma;
import br.edu.ppg.hub.core.domain.enums.StatusPrograma;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para atualização de um Programa existente.
 * Todos os campos são opcionais (atualização parcial).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaUpdateDTO {

    @Size(max = 20, message = "Código CAPES deve ter no máximo 20 caracteres")
    private String codigoCapes;

    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    @Size(max = 20, message = "Sigla deve ter no máximo 20 caracteres")
    private String sigla;

    @Size(max = 255, message = "Área de concentração deve ter no máximo 255 caracteres")
    private String areaConcentracao;

    private NivelPrograma nivel;

    private ModalidadePrograma modalidade;

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
    private Integer creditosMinimosMestrado;

    @Min(value = 0, message = "Créditos mínimos doutorado deve ser no mínimo 0")
    private Integer creditosMinimosDoutorado;

    @Min(value = 1, message = "Prazo máximo mestrado deve ser no mínimo 1 mês")
    private Integer prazoMaximoMestrado;

    @Min(value = 1, message = "Prazo máximo doutorado deve ser no mínimo 1 mês")
    private Integer prazoMaximoDoutorado;

    private String openalexInstitutionId;

    private StatusPrograma status;

    private String configuracoes;
}
