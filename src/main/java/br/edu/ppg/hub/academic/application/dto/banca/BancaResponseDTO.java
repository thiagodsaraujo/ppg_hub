package br.edu.ppg.hub.academic.application.dto.banca;

import br.edu.ppg.hub.academic.domain.enums.ResultadoBanca;
import br.edu.ppg.hub.academic.domain.enums.TipoBanca;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * DTO de resposta para uma banca examinadora.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BancaResponseDTO {

    private Long id;

    // Relacionamentos
    private Long trabalhoConclusaoId;
    private String trabalhoConclusaoTitulo;

    private Long discenteId;
    private String discenteNome;

    private Long presidenteId;
    private String presidenteNome;

    private Long secretarioId;
    private String secretarioNome;

    // Tipo e Status
    private TipoBanca tipo;
    private String status;

    // Agendamento
    private LocalDate dataAgendada;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private String localRealizacao;
    private String modalidade;
    private String linkVirtual;
    private LocalDate dataRealizacao;

    // Documentação
    private String ataNumero;
    private String ataArquivo;
    private Map<String, Object> pauta;
    private Map<String, Object> ata;

    // Resultado
    private ResultadoBanca resultado;
    private BigDecimal notaFinal;
    private Integer prazoCorrecoesDias;
    private String correcoesExigidas;
    private String observacoesBanca;
    private String recomendacoes;
    private Boolean sugestaoPublicacao;

    // Auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
