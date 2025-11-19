package br.edu.ppg.hub.integration.reports.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO de resposta consolidada para dashboard do programa.
 * <p>
 * Agrega todas as estatísticas, métricas de produtividade e dados de evasão
 * em uma única resposta para visualização em dashboards.
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard consolidado do programa de pós-graduação")
public class DashboardResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Estatísticas gerais do programa")
    private ProgramaStatsDTO estatisticas;

    @Schema(description = "Top docentes por produtividade (ordenado por H-index)")
    private List<ProducaoDocenteDTO> topDocentes;

    @Schema(description = "Análise de evasão e conclusão por ano de ingresso")
    private List<EvasaoConclusaoDTO> evasaoPorAno;

    @Schema(description = "Dados adicionais para gráficos e visualizações")
    private Map<String, Object> graficos;

    /**
     * Cria um dashboard vazio.
     *
     * @return Dashboard sem dados
     */
    public static DashboardResponseDTO empty() {
        return DashboardResponseDTO.builder()
                .estatisticas(null)
                .topDocentes(List.of())
                .evasaoPorAno(List.of())
                .graficos(Map.of())
                .build();
    }

    /**
     * Verifica se o dashboard possui dados.
     *
     * @return true se há dados disponíveis
     */
    public boolean hasData() {
        return estatisticas != null ||
               (topDocentes != null && !topDocentes.isEmpty()) ||
               (evasaoPorAno != null && !evasaoPorAno.isEmpty());
    }

    /**
     * Retorna o número total de docentes no ranking.
     *
     * @return Número de docentes
     */
    public int getTotalDocentesRanking() {
        return topDocentes != null ? topDocentes.size() : 0;
    }

    /**
     * Retorna o número total de coortes analisadas.
     *
     * @return Número de coortes
     */
    public int getTotalCoortesAnalisadas() {
        return evasaoPorAno != null ? evasaoPorAno.size() : 0;
    }
}
