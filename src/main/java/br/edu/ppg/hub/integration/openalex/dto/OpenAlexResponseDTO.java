package br.edu.ppg.hub.integration.openalex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO genérico para respostas paginadas da API OpenAlex.
 *
 * @param <T> tipo do resultado (OpenAlexAuthorDTO ou OpenAlexWorkDTO)
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAlexResponseDTO<T> {

    /**
     * Metadados da resposta
     */
    private Meta meta;

    /**
     * Lista de resultados
     */
    private List<T> results;

    /**
     * Classe interna que representa metadados da resposta
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        /**
         * Número total de resultados encontrados
         */
        private Integer count;

        /**
         * Cursor para próxima página (paginação)
         */
        private String next_cursor;

        /**
         * Verifica se há mais páginas
         *
         * @return true se houver mais resultados
         */
        public boolean hasNextPage() {
            return next_cursor != null && !next_cursor.isEmpty();
        }
    }

    /**
     * Verifica se a resposta contém resultados
     *
     * @return true se houver resultados
     */
    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }

    /**
     * Retorna o número de resultados na página atual
     *
     * @return número de resultados
     */
    public int getResultsSize() {
        return results != null ? results.size() : 0;
    }
}
