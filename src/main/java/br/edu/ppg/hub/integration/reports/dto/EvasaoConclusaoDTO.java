package br.edu.ppg.hub.integration.reports.dto;

import br.edu.ppg.hub.academic.domain.enums.TipoCurso;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO para análise de evasão e conclusão por coorte de ingresso.
 * <p>
 * Permite identificar padrões de evasão e conclusão ao longo do tempo,
 * segmentados por programa, tipo de curso e ano de ingresso.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Análise de evasão e conclusão por coorte de ingresso")
public class EvasaoConclusaoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID do programa", example = "1")
    private Long programaId;

    @Schema(description = "Nome do programa", example = "Programa de Pós-Graduação em Ciência da Computação")
    private String programaNome;

    @Schema(description = "Sigla do programa", example = "PPGCC")
    private String programaSigla;

    @Schema(description = "Tipo de curso", example = "MESTRADO")
    private TipoCurso tipoCurso;

    @Schema(description = "Ano de ingresso da coorte", example = "2020")
    private Integer anoIngresso;

    @Schema(description = "Total de alunos que ingressaram", example = "25")
    private Integer totalIngressantes;

    @Schema(description = "Total de alunos titulados", example = "18")
    private Integer totalTitulados;

    @Schema(description = "Total de alunos evadidos (desligados + cancelados)", example = "3")
    private Integer totalEvadidos;

    @Schema(description = "Total de alunos ainda cursando", example = "4")
    private Integer totalCursando;

    @Schema(description = "Total de alunos com matrícula trancada", example = "0")
    private Integer totalTrancados;

    @Schema(description = "Percentual de titulados", example = "72.00")
    private BigDecimal taxaConclusao;

    @Schema(description = "Percentual de evadidos", example = "12.00")
    private BigDecimal taxaEvasao;

    @Schema(description = "Percentual de alunos ainda cursando", example = "16.00")
    private BigDecimal taxaCursando;

    @Schema(description = "Tempo médio em anos para titulação", example = "2.3")
    private BigDecimal tempoMedioTitulacao;

    /**
     * Verifica se a coorte está dentro do prazo máximo para conclusão.
     *
     * @param anoAtual Ano atual para cálculo
     * @return true se ainda está dentro do prazo
     */
    public boolean isDentroDoPrazo(int anoAtual) {
        if (anoIngresso == null || tipoCurso == null) {
            return false;
        }
        int anosDecorridos = anoAtual - anoIngresso;
        int prazoMaximoAnos = tipoCurso.getPrazoMaximoMeses() / 12;
        return anosDecorridos <= prazoMaximoAnos;
    }

    /**
     * Calcula a taxa de retenção (cursando + titulados).
     *
     * @return Percentual de alunos que não evadiram
     */
    public BigDecimal calcularTaxaRetencao() {
        if (totalIngressantes == null || totalIngressantes == 0) {
            return BigDecimal.ZERO;
        }
        int retidos = (totalCursando != null ? totalCursando : 0) +
                      (totalTitulados != null ? totalTitulados : 0);
        return BigDecimal.valueOf(retidos)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalIngressantes), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Verifica se a taxa de evasão está acima do limite crítico (> 20%).
     *
     * @return true se taxa de evasão > 20%
     */
    public boolean isEvasaoCritica() {
        return taxaEvasao != null && taxaEvasao.compareTo(BigDecimal.valueOf(20)) > 0;
    }

    /**
     * Verifica se a taxa de conclusão está abaixo do esperado (< 60%).
     *
     * @return true se taxa de conclusão < 60%
     */
    public boolean isConclusaoBaixa() {
        return taxaConclusao != null && taxaConclusao.compareTo(BigDecimal.valueOf(60)) < 0;
    }

    /**
     * Calcula a projeção de titulados considerando os que ainda estão cursando.
     *
     * @param taxaSucessoEsperada Taxa de sucesso esperada para os que estão cursando
     * @return Número projetado de titulados
     */
    public int calcularProjecaoTitulados(double taxaSucessoEsperada) {
        int titulados = totalTitulados != null ? totalTitulados : 0;
        int cursando = totalCursando != null ? totalCursando : 0;
        return titulados + (int) Math.round(cursando * taxaSucessoEsperada);
    }
}
