package br.edu.ppg.hub.integration.openalex.controller;

import br.edu.ppg.hub.integration.openalex.dto.OpenAlexAuthorDTO;
import br.edu.ppg.hub.integration.openalex.dto.OpenAlexWorkDTO;
import br.edu.ppg.hub.integration.openalex.service.OpenAlexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para integração com OpenAlex.
 *
 * Expõe endpoints para:
 * - Sincronizar métricas de docentes
 * - Buscar dados de autores e publicações
 * - Consultar informações acadêmicas
 *
 * @author PPG Hub
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/integracoes/openalex")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OpenAlex", description = "Endpoints para integração com a API OpenAlex")
@SecurityRequirement(name = "bearer-jwt")
public class OpenAlexController {

    private final OpenAlexService openAlexService;

    /**
     * Sincroniza métricas de um docente específico.
     *
     * Busca dados no OpenAlex usando o ORCID do docente e atualiza suas métricas.
     *
     * @param docenteId ID do docente
     * @return resposta de sucesso
     */
    @PostMapping("/sync/docente/{docenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(
        summary = "Sincronizar métricas de um docente",
        description = "Busca e atualiza métricas acadêmicas de um docente usando a API OpenAlex. " +
                      "Requer que o docente tenha ORCID cadastrado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métricas sincronizadas com sucesso"),
        @ApiResponse(responseCode = "400", description = "Docente não possui ORCID cadastrado"),
        @ApiResponse(responseCode = "404", description = "Docente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro ao sincronizar com OpenAlex")
    })
    public ResponseEntity<Map<String, String>> syncDocenteMetrics(
            @Parameter(description = "ID do docente", required = true)
            @PathVariable Long docenteId
    ) {
        log.info("Requisição para sincronizar métricas do docente: {}", docenteId);

        openAlexService.syncDocenteMetrics(docenteId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Métricas sincronizadas com sucesso");
        response.put("docenteId", docenteId.toString());
        response.put("fonte", "OpenAlex");

        return ResponseEntity.ok(response);
    }

    /**
     * Sincroniza métricas de todos os docentes que possuem ORCID.
     *
     * Apenas ADMIN pode executar esta operação.
     * Processo pode demorar dependendo da quantidade de docentes.
     *
     * @return resposta de sucesso
     */
    @PostMapping("/sync/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Sincronizar métricas de todos os docentes",
        description = "Sincroniza métricas de todos os docentes que possuem ORCID cadastrado. " +
                      "Operação pode demorar. Apenas ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Sincronização iniciada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - Apenas ADMIN")
    })
    public ResponseEntity<Map<String, String>> syncAllDocentes() {
        log.info("Requisição para sincronizar métricas de todos os docentes");

        // Executar sincronização
        openAlexService.syncAllDocentesMetrics();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Sincronização de todos os docentes concluída");
        response.put("fonte", "OpenAlex");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Busca dados de um autor no OpenAlex pelo ORCID.
     *
     * @param orcid ORCID do autor (ex: 0000-0001-2345-6789)
     * @return dados do autor
     */
    @GetMapping("/author/{orcid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(
        summary = "Buscar autor por ORCID",
        description = "Consulta dados de um autor no OpenAlex usando o ORCID. " +
                      "Retorna informações sobre publicações, citações e H-index."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autor encontrado"),
        @ApiResponse(responseCode = "404", description = "Autor não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro ao consultar OpenAlex")
    })
    public ResponseEntity<OpenAlexAuthorDTO> getAuthorByOrcid(
            @Parameter(description = "ORCID do autor", required = true)
            @PathVariable String orcid
    ) {
        log.info("Requisição para buscar autor com ORCID: {}", orcid);

        OpenAlexAuthorDTO author = openAlexService.searchAuthorByOrcid(orcid);

        if (author == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(author);
    }

    /**
     * Busca uma publicação no OpenAlex pelo DOI.
     *
     * @param doi DOI da publicação (ex: 10.1234/example)
     * @return dados da publicação
     */
    @GetMapping("/work/{doi}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(
        summary = "Buscar publicação por DOI",
        description = "Consulta dados de uma publicação no OpenAlex usando o DOI. " +
                      "Retorna informações sobre título, autores, ano e citações."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Publicação encontrada"),
        @ApiResponse(responseCode = "404", description = "Publicação não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro ao consultar OpenAlex")
    })
    public ResponseEntity<OpenAlexWorkDTO> getWorkByDoi(
            @Parameter(description = "DOI da publicação", required = true)
            @PathVariable String doi
    ) {
        log.info("Requisição para buscar publicação com DOI: {}", doi);

        OpenAlexWorkDTO work = openAlexService.getWorkByDoi(doi);

        if (work == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(work);
    }

    /**
     * Busca todas as publicações de um autor pelo ORCID.
     *
     * @param orcid ORCID do autor
     * @return lista de publicações
     */
    @GetMapping("/author/{orcid}/works")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(
        summary = "Buscar publicações de um autor",
        description = "Retorna todas as publicações de um autor no OpenAlex usando o ORCID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Publicações encontradas"),
        @ApiResponse(responseCode = "404", description = "Autor não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro ao consultar OpenAlex")
    })
    public ResponseEntity<List<OpenAlexWorkDTO>> getAuthorWorks(
            @Parameter(description = "ORCID do autor", required = true)
            @PathVariable String orcid
    ) {
        log.info("Requisição para buscar publicações do autor com ORCID: {}", orcid);

        // Primeiro buscar o autor para pegar o ID do OpenAlex
        OpenAlexAuthorDTO author = openAlexService.searchAuthorByOrcid(orcid);

        if (author == null) {
            return ResponseEntity.notFound().build();
        }

        // Extrair ID do OpenAlex
        String authorId = author.getId();
        if (authorId.contains("/")) {
            String[] parts = authorId.split("/");
            authorId = parts[parts.length - 1];
        }

        // Buscar trabalhos
        List<OpenAlexWorkDTO> works = openAlexService.searchWorksByAuthor(authorId);

        return ResponseEntity.ok(works);
    }
}
