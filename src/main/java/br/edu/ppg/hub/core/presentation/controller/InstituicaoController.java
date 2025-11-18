package br.edu.ppg.hub.core.presentation.controller;

import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoCreateDTO;
import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoResponseDTO;
import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoUpdateDTO;
import br.edu.ppg.hub.core.application.service.InstituicaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para endpoints de Instituição.
 *
 * Fornece API RESTful completa para CRUD e operações avançadas
 * de instituições de ensino.
 */
@RestController
@RequestMapping("/api/v1/instituicoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Instituições", description = "API para gerenciamento de instituições de ensino")
public class InstituicaoController {

    private final InstituicaoService service;

    /**
     * Cria uma nova instituição
     */
    @PostMapping
    @Operation(summary = "Criar instituição", description = "Cria uma nova instituição de ensino")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Instituição criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Instituição já existe (código ou CNPJ duplicado)")
    })
    public ResponseEntity<InstituicaoResponseDTO> create(
            @Valid @RequestBody InstituicaoCreateDTO dto) {

        log.info("POST /api/v1/instituicoes - Criando instituição: {}", dto.getCodigo());

        InstituicaoResponseDTO created = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Busca instituição por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Busca instituição pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituição encontrada"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<InstituicaoResponseDTO> findById(
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long id) {

        log.debug("GET /api/v1/instituicoes/{} - Buscando por ID", id);

        InstituicaoResponseDTO response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Busca instituição por código
     */
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar por código", description = "Busca instituição pelo código único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituição encontrada"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<InstituicaoResponseDTO> findByCodigo(
            @Parameter(description = "Código da instituição", example = "UEPB", required = true)
            @PathVariable String codigo) {

        log.debug("GET /api/v1/instituicoes/codigo/{} - Buscando por código", codigo);

        InstituicaoResponseDTO response = service.findByCodigo(codigo);

        return ResponseEntity.ok(response);
    }

    /**
     * Busca instituição por CNPJ
     */
    @GetMapping("/cnpj/{cnpj}")
    @Operation(summary = "Buscar por CNPJ", description = "Busca instituição pelo CNPJ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituição encontrada"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<InstituicaoResponseDTO> findByCnpj(
            @Parameter(description = "CNPJ da instituição", example = "12.345.678/0001-90", required = true)
            @PathVariable String cnpj) {

        log.debug("GET /api/v1/instituicoes/cnpj/{} - Buscando por CNPJ", cnpj);

        InstituicaoResponseDTO response = service.findByCnpj(cnpj);

        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas as instituições com paginação
     */
    @GetMapping
    @Operation(summary = "Listar instituições", description = "Lista todas as instituições com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<InstituicaoResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "nomeAbreviado", direction = Sort.Direction.ASC)
            Pageable pageable) {

        log.debug("GET /api/v1/instituicoes - Listando todas");

        Page<InstituicaoResponseDTO> response = service.findAll(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Lista apenas instituições ativas
     */
    @GetMapping("/ativas")
    @Operation(summary = "Listar instituições ativas", description = "Lista apenas instituições ativas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<InstituicaoResponseDTO>> findAllAtivas(
            @PageableDefault(size = 20, sort = "nomeAbreviado", direction = Sort.Direction.ASC)
            Pageable pageable) {

        log.debug("GET /api/v1/instituicoes/ativas - Listando ativas");

        Page<InstituicaoResponseDTO> response = service.findAllAtivas(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Busca instituições por termo livre
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar instituições", description = "Busca instituições por termo livre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados da busca")
    })
    public ResponseEntity<Page<InstituicaoResponseDTO>> search(
            @Parameter(description = "Termo de busca", required = true)
            @RequestParam String termo,

            @Parameter(description = "Buscar apenas ativas", example = "true")
            @RequestParam(defaultValue = "true") boolean apenasAtivas,

            @PageableDefault(size = 20, sort = "nomeAbreviado", direction = Sort.Direction.ASC)
            Pageable pageable) {

        log.debug("GET /api/v1/instituicoes/search?termo={}&apenasAtivas={}", termo, apenasAtivas);

        Page<InstituicaoResponseDTO> response = service.search(termo, apenasAtivas, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Lista instituições por tipo
     */
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar por tipo", description = "Lista instituições de um tipo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<List<InstituicaoResponseDTO>> findByTipo(
            @Parameter(description = "Tipo da instituição", example = "Estadual", required = true)
            @PathVariable String tipo) {

        log.debug("GET /api/v1/instituicoes/tipo/{} - Listando por tipo", tipo);

        List<InstituicaoResponseDTO> response = service.findByTipo(tipo);

        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza uma instituição
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar instituição", description = "Atualiza dados de uma instituição")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituição atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito (código ou CNPJ já existe)")
    })
    public ResponseEntity<InstituicaoResponseDTO> update(
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long id,

            @Valid @RequestBody InstituicaoUpdateDTO dto) {

        log.info("PUT /api/v1/instituicoes/{} - Atualizando instituição", id);

        InstituicaoResponseDTO updated = service.update(id, dto);

        return ResponseEntity.ok(updated);
    }

    /**
     * Remove uma instituição
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover instituição", description = "Remove permanentemente uma instituição")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Instituição removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long id) {

        log.info("DELETE /api/v1/instituicoes/{} - Removendo instituição", id);

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Desativa uma instituição (soft delete)
     */
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desativar instituição", description = "Desativa uma instituição (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituição desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<InstituicaoResponseDTO> deactivate(
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long id) {

        log.info("PATCH /api/v1/instituicoes/{}/deactivate - Desativando instituição", id);

        InstituicaoResponseDTO response = service.deactivate(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Reativa uma instituição
     */
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Reativar instituição", description = "Reativa uma instituição desativada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instituição reativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<InstituicaoResponseDTO> activate(
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable Long id) {

        log.info("PATCH /api/v1/instituicoes/{}/activate - Reativando instituição", id);

        InstituicaoResponseDTO response = service.activate(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Retorna estatísticas gerais
     */
    @GetMapping("/stats")
    @Operation(summary = "Estatísticas", description = "Retorna estatísticas de instituições")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    })
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.debug("GET /api/v1/instituicoes/stats - Obtendo estatísticas");

        Map<String, Object> stats = Map.of(
                "total", service.countTotal(),
                "ativas", service.countAtivas(),
                "por_tipo", service.getEstatisticasPorTipo()
        );

        return ResponseEntity.ok(stats);
    }
}
