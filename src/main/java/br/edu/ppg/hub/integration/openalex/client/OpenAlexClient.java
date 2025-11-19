package br.edu.ppg.hub.integration.openalex.client;

import br.edu.ppg.hub.config.FeignConfig;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexAuthorDTO;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexResponseDTO;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexWorkDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign Client para integração com a API OpenAlex.
 *
 * OpenAlex é uma base de dados aberta de publicações científicas,
 * autores, instituições e conceitos acadêmicos.
 *
 * @author PPG Hub
 * @since 1.0
 * @see <a href="https://docs.openalex.org/">OpenAlex API Documentation</a>
 */
@FeignClient(
    name = "openalex",
    url = "${openalex.api.url:https://api.openalex.org}",
    configuration = FeignConfig.class
)
public interface OpenAlexClient {

    /**
     * Busca um autor pelo ID do OpenAlex.
     *
     * @param authorId ID do autor no OpenAlex (ex: A1234567890)
     * @return dados do autor
     */
    @GetMapping("/authors/{id}")
    OpenAlexAuthorDTO getAuthor(@PathVariable("id") String authorId);

    /**
     * Busca autores com filtros e pesquisa.
     *
     * Exemplos de filtro:
     * - orcid:0000-0001-2345-6789
     * - display_name.search:John Smith
     *
     * @param search texto de busca (opcional)
     * @param filter filtros da API OpenAlex (opcional)
     * @param perPage número de resultados por página (padrão: 25, máx: 200)
     * @return resposta paginada com autores
     */
    @GetMapping("/authors")
    OpenAlexResponseDTO<OpenAlexAuthorDTO> searchAuthors(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String filter,
        @RequestParam(name = "per-page", required = false, defaultValue = "25") Integer perPage
    );

    /**
     * Busca trabalhos (publicações) com filtros.
     *
     * Exemplos de filtro:
     * - author.id:A1234567890
     * - publication_year:2023
     * - type:article
     *
     * @param filter filtros da API OpenAlex
     * @param search texto de busca (opcional)
     * @param perPage número de resultados por página (padrão: 25, máx: 200)
     * @return resposta paginada com trabalhos
     */
    @GetMapping("/works")
    OpenAlexResponseDTO<OpenAlexWorkDTO> searchWorks(
        @RequestParam(required = false) String filter,
        @RequestParam(required = false) String search,
        @RequestParam(name = "per-page", required = false, defaultValue = "25") Integer perPage
    );

    /**
     * Busca um trabalho pelo DOI.
     *
     * @param doi DOI da publicação (ex: 10.1234/example)
     * @return dados do trabalho
     */
    @GetMapping("/works/doi:{doi}")
    OpenAlexWorkDTO getWorkByDoi(@PathVariable("doi") String doi);
}
