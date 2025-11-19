package br.edu.ppg.hub.integration.reports.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO para métricas de produtividade e atividades docentes.
 * <p>
 * Consolida informações sobre orientações, disciplinas ministradas, bancas
 * e métricas bibliométricas (OpenAlex).
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Métricas consolidadas de produtividade docente")
public class ProducaoDocenteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID do docente", example = "1")
    private Long docenteId;

    @Schema(description = "ID do programa ao qual o docente está vinculado", example = "1")
    private Long programaId;

    @Schema(description = "Nome completo do docente", example = "Dr. João da Silva")
    private String docenteNome;

    @Schema(description = "E-mail institucional do docente", example = "joao.silva@universidade.edu.br")
    private String docenteEmail;

    @Schema(description = "Categoria do docente", example = "PERMANENTE")
    private String docenteCategoria;

    @Schema(description = "Total de orientandos (todos os status)", example = "15")
    private Integer totalOrientandos;

    @Schema(description = "Orientandos com status CURSANDO", example = "8")
    private Integer orientandosAtivos;

    @Schema(description = "Orientandos com status TITULADO", example = "6")
    private Integer orientandosTitulados;

    @Schema(description = "Orientandos evadidos (DESLIGADO ou CANCELADO)", example = "1")
    private Integer orientandosEvadidos;

    @Schema(description = "Total de ofertas de disciplinas ministradas", example = "12")
    private Integer disciplinasMinistradas;

    @Schema(description = "Total de bancas participadas", example = "18")
    private Integer bancasParticipadas;

    @Schema(description = "Bancas de qualificação", example = "10")
    private Integer bancasQualificacao;

    @Schema(description = "Bancas de defesa", example = "8")
    private Integer bancasDefesa;

    @Schema(description = "Total de publicações indexadas (OpenAlex)", example = "45")
    private Integer totalPublicacoes;

    @Schema(description = "Total de citações recebidas (OpenAlex)", example = "320")
    private Integer totalCitacoes;

    @Schema(description = "Índice H do docente (OpenAlex)", example = "12")
    private Integer hIndex;

    @Schema(description = "Índice i10 do docente (OpenAlex)", example = "18")
    private Integer i10Index;

    /**
     * Calcula a taxa de sucesso nas orientações.
     *
     * @return Percentual de orientandos titulados em relação ao total
     */
    public BigDecimal calcularTaxaSucessoOrientacao() {
        if (totalOrientandos == null || totalOrientandos == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(orientandosTitulados != null ? orientandosTitulados : 0)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalOrientandos), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calcula a taxa de evasão nas orientações.
     *
     * @return Percentual de orientandos evadidos em relação ao total
     */
    public BigDecimal calcularTaxaEvasaoOrientacao() {
        if (totalOrientandos == null || totalOrientandos == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(orientandosEvadidos != null ? orientandosEvadidos : 0)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalOrientandos), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calcula a média de citações por publicação.
     *
     * @return Média de citações por publicação
     */
    public BigDecimal calcularMediaCitacoesPorPublicacao() {
        if (totalPublicacoes == null || totalPublicacoes == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalCitacoes != null ? totalCitacoes : 0)
                .divide(BigDecimal.valueOf(totalPublicacoes), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Verifica se o docente é altamente produtivo (H-index >= 10).
     *
     * @return true se H-index >= 10
     */
    public boolean isAltamenteProdutivo() {
        return hIndex != null && hIndex >= 10;
    }
}
