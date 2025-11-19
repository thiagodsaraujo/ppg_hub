package br.edu.ppg.hub.academic.application.dto.matricula_disciplina;

import br.edu.ppg.hub.academic.domain.enums.StatusMatricula;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta com todos os dados de uma matrícula em disciplina
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatriculaDisciplinaResponseDTO {

    private Long id;

    // Dados do discente
    private Long discenteId;
    private String discenteNome;
    private String discenteEmail;
    private String discenteMatricula;

    // Dados da oferta
    private Long ofertaDisciplinaId;
    private String ofertaPeriodo;
    private String ofertaTurma;

    // Dados da disciplina
    private Long disciplinaId;
    private String disciplinaCodigo;
    private String disciplinaNome;
    private Integer disciplinaCreditos;

    // Dados do docente
    private Long docenteResponsavelId;
    private String docenteResponsavelNome;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataMatricula;

    private StatusMatricula situacao;

    private String avaliacoes;

    private BigDecimal frequenciaPercentual;

    private BigDecimal notaFinal;

    private String conceito;

    private String statusFinal;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataResultado;

    private String observacoes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Campos calculados
    private Boolean aprovado;
    private Boolean reprovado;
    private Boolean ativa;
    private Boolean trancada;
    private Boolean podeTranscar;
    private Boolean atingiuFrequenciaMinima;
    private Boolean atingiuNotaMinima;
}
