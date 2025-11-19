package br.edu.ppg.hub.integration.reports.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO para estatísticas consolidadas do programa de pós-graduação.
 * <p>
 * Contém métricas agregadas sobre docentes, discentes, disciplinas e desempenho acadêmico.
 * Utilizado para dashboards e relatórios gerenciais.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estatísticas consolidadas do programa de pós-graduação")
public class ProgramaStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID do programa", example = "1")
    private Long programaId;

    @Schema(description = "Nome do programa", example = "Programa de Pós-Graduação em Ciência da Computação")
    private String programaNome;

    @Schema(description = "Sigla do programa", example = "PPGCC")
    private String programaSigla;

    @Schema(description = "Total de docentes vinculados ao programa", example = "25")
    private Integer totalDocentes;

    @Schema(description = "Total de docentes permanentes", example = "20")
    private Integer docentesPermanentes;

    @Schema(description = "Total de discentes no programa", example = "80")
    private Integer totalDiscentes;

    @Schema(description = "Total de alunos de mestrado", example = "50")
    private Integer mestrandos;

    @Schema(description = "Total de alunos de doutorado", example = "30")
    private Integer doutorandos;

    @Schema(description = "Total de discentes ativos (status CURSANDO)", example = "65")
    private Integer discentesAtivos;

    @Schema(description = "Total de discentes titulados", example = "120")
    private Integer titulados;

    @Schema(description = "Total de disciplinas ofertadas pelo programa", example = "35")
    private Integer totalDisciplinas;

    @Schema(description = "Total de ofertas de disciplina com matrículas abertas", example = "12")
    private Integer ofertasAtivas;

    @Schema(description = "Média das notas finais dos alunos aprovados", example = "8.5")
    private BigDecimal mediaNotas;

    /**
     * Calcula a taxa de docentes permanentes.
     *
     * @return Percentual de docentes permanentes em relação ao total
     */
    public BigDecimal calcularTaxaDocentesPermanentes() {
        if (totalDocentes == null || totalDocentes == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(docentesPermanentes != null ? docentesPermanentes : 0)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalDocentes), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calcula a taxa de discentes ativos.
     *
     * @return Percentual de discentes ativos em relação ao total
     */
    public BigDecimal calcularTaxaDiscentesAtivos() {
        if (totalDiscentes == null || totalDiscentes == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(discentesAtivos != null ? discentesAtivos : 0)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalDiscentes), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calcula a relação orientando/orientador.
     *
     * @return Número médio de orientandos por docente permanente
     */
    public BigDecimal calcularRelacaoOrientandoPorDocente() {
        if (docentesPermanentes == null || docentesPermanentes == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(discentesAtivos != null ? discentesAtivos : 0)
                .divide(BigDecimal.valueOf(docentesPermanentes), 2, BigDecimal.ROUND_HALF_UP);
    }
}
