package br.edu.ppg.hub.core.application.dto.programa;

import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoResponseDTO;
import br.edu.ppg.hub.core.domain.enums.ModalidadePrograma;
import br.edu.ppg.hub.core.domain.enums.NivelPrograma;
import br.edu.ppg.hub.core.domain.enums.StatusPrograma;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta com dados de um Programa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaResponseDTO {

    private Long id;
    private InstituicaoResponseDTO instituicao;
    private String codigoCapes;
    private String nome;
    private String sigla;
    private String areaConcentracao;
    private NivelPrograma nivel;
    private ModalidadePrograma modalidade;
    private LocalDate inicioFuncionamento;
    private Integer conceitoCapes;
    private LocalDate dataUltimaAvaliacao;
    private String trienioAvaliacao;
    private Long coordenadorId;
    private String coordenadorNome;
    private Long coordenadorAdjuntoId;
    private String coordenadorAdjuntoNome;
    private LocalDate mandatoInicio;
    private LocalDate mandatoFim;
    private Integer creditosMinimosMestrado;
    private Integer creditosMinimosDoutorado;
    private Integer prazoMaximoMestrado;
    private Integer prazoMaximoDoutorado;
    private String openalexInstitutionId;
    private StatusPrograma status;
    private String configuracoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Campos calculados
    private Boolean ativo;
    private Boolean ofereceMestrado;
    private Boolean ofereceDoutorado;
    private Boolean mandatoVigente;
}
