package br.edu.ppg.hub.integration.openalex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO que representa um autor da API OpenAlex.
 *
 * @author PPG Hub
 * @since 1.0
 * @see <a href="https://docs.openalex.org/api-entities/authors">OpenAlex Authors API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAlexAuthorDTO {

    /**
     * ID único do autor no OpenAlex (ex: https://openalex.org/A1234567890)
     */
    private String id;

    /**
     * ORCID do autor (ex: https://orcid.org/0000-0001-2345-6789)
     */
    private String orcid;

    /**
     * Nome completo do autor
     */
    private String display_name;

    /**
     * Total de trabalhos (publicações) do autor
     */
    private Integer works_count;

    /**
     * Total de citações recebidas
     */
    private Integer cited_by_count;

    /**
     * Estatísticas resumidas do autor
     * Contém: h_index, i10_index, 2yr_mean_citedness, etc.
     */
    private Map<String, Object> summary_stats;

    /**
     * Extrai o H-index das estatísticas
     *
     * @return H-index ou null se não disponível
     */
    public Integer getHIndex() {
        if (summary_stats != null && summary_stats.containsKey("h_index")) {
            Object hIndex = summary_stats.get("h_index");
            if (hIndex instanceof Number) {
                return ((Number) hIndex).intValue();
            }
        }
        return null;
    }

    /**
     * Extrai o i10-index das estatísticas
     *
     * @return i10-index ou null se não disponível
     */
    public Integer getI10Index() {
        if (summary_stats != null && summary_stats.containsKey("i10_index")) {
            Object i10Index = summary_stats.get("i10_index");
            if (i10Index instanceof Number) {
                return ((Number) i10Index).intValue();
            }
        }
        return null;
    }

    /**
     * Extrai a média de citações de 2 anos
     *
     * @return média de citações ou null se não disponível
     */
    public Double getTwoYearMeanCitedness() {
        if (summary_stats != null && summary_stats.containsKey("2yr_mean_citedness")) {
            Object citedness = summary_stats.get("2yr_mean_citedness");
            if (citedness instanceof Number) {
                return ((Number) citedness).doubleValue();
            }
        }
        return null;
    }
}
