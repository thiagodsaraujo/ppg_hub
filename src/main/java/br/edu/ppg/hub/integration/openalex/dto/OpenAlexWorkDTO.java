package br.edu.ppg.hub.integration.openalex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa um trabalho (publicação) da API OpenAlex.
 *
 * @author PPG Hub
 * @since 1.0
 * @see <a href="https://docs.openalex.org/api-entities/works">OpenAlex Works API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAlexWorkDTO {

    /**
     * ID único do trabalho no OpenAlex (ex: https://openalex.org/W1234567890)
     */
    private String id;

    /**
     * DOI da publicação (ex: https://doi.org/10.1234/example)
     */
    private String doi;

    /**
     * Título da publicação
     */
    private String title;

    /**
     * Ano de publicação
     */
    private Integer publication_year;

    /**
     * Número de citações recebidas
     */
    private Integer cited_by_count;

    /**
     * Lista de conceitos/tópicos relacionados à publicação
     */
    private List<String> concepts;

    /**
     * Tipo de publicação (article, book-chapter, dissertation, etc.)
     */
    private String type;

    /**
     * Verifica se é um artigo de periódico
     *
     * @return true se for artigo
     */
    public boolean isArticle() {
        return "article".equalsIgnoreCase(type);
    }

    /**
     * Verifica se foi publicado nos últimos 5 anos
     *
     * @return true se foi publicado nos últimos 5 anos
     */
    public boolean isRecent() {
        if (publication_year == null) {
            return false;
        }
        int currentYear = java.time.Year.now().getValue();
        return publication_year >= (currentYear - 5);
    }
}
