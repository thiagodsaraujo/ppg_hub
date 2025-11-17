package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.DiscenteCreateRequest;
import com.ppghub.application.dto.request.DiscenteUpdateRequest;
import com.ppghub.application.dto.response.DiscenteResponse;
import com.ppghub.application.dto.response.PagedResponse;
import com.ppghub.domain.service.DiscenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Discentes.
 */
@RestController
@RequestMapping("/v1/discentes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Discentes", description = "Endpoints para gerenciamento de discentes (estudantes de pós-graduação)")
public class DiscenteController {

    private final DiscenteService discenteService;

    @GetMapping
    @Operation(summary = "Listar todos os discentes", description = "Retorna lista de todos os discentes")
    public ResponseEntity<List<DiscenteResponse>> listarTodos() {
        log.info("Listando todos os discentes");
        return ResponseEntity.ok(discenteService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar discente por ID", description = "Retorna um discente específico pelo ID")
    public ResponseEntity<DiscenteResponse> buscarPorId(
            @Parameter(description = "ID do discente") @PathVariable Long id) {
        log.info("Buscando discente por ID: {}", id);
        return discenteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/matricula/{matricula}")
    @Operation(summary = "Buscar discente por matrícula", description = "Retorna discente pela matrícula")
    public ResponseEntity<DiscenteResponse> buscarPorMatricula(
            @Parameter(description = "Matrícula do discente") @PathVariable String matricula) {
        log.info("Buscando discente por matrícula: {}", matricula);
        return discenteService.findByMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar discente por email", description = "Retorna discente pelo email")
    public ResponseEntity<DiscenteResponse> buscarPorEmail(
            @Parameter(description = "Email do discente") @PathVariable String email) {
        log.info("Buscando discente por email: {}", email);
        return discenteService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/programa/{programaId}")
    @Operation(summary = "Listar discentes por programa", description = "Retorna discentes de um programa")
    public ResponseEntity<List<DiscenteResponse>> listarPorPrograma(
            @Parameter(description = "ID do programa") @PathVariable Long programaId) {
        log.info("Listando discentes do programa ID: {}", programaId);
        return ResponseEntity.ok(discenteService.findByPrograma(programaId));
    }

    @GetMapping("/programa/{programaId}/page")
    @Operation(summary = "Listar discentes por programa (paginado)", description = "Retorna discentes de um programa com paginação")
    public ResponseEntity<PagedResponse<DiscenteResponse>> listarPorProgramaPaginado(
            @Parameter(description = "ID do programa") @PathVariable Long programaId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Listando discentes do programa ID: {} com paginação", programaId);
        return ResponseEntity.ok(PagedResponse.of(discenteService.findByPrograma(programaId, pageable)));
    }

    @GetMapping("/orientador/{orientadorId}")
    @Operation(summary = "Listar discentes por orientador", description = "Retorna discentes de um orientador")
    public ResponseEntity<List<DiscenteResponse>> listarPorOrientador(
            @Parameter(description = "ID do orientador") @PathVariable Long orientadorId) {
        log.info("Listando discentes do orientador ID: {}", orientadorId);
        return ResponseEntity.ok(discenteService.findByOrientador(orientadorId));
    }

    @GetMapping("/programa/{programaId}/ativos")
    @Operation(summary = "Listar discentes ativos por programa", description = "Retorna discentes ativos de um programa")
    public ResponseEntity<List<DiscenteResponse>> listarAtivosPorPrograma(
            @Parameter(description = "ID do programa") @PathVariable Long programaId) {
        log.info("Listando discentes ativos do programa ID: {}", programaId);
        return ResponseEntity.ok(discenteService.findDiscentesAtivos(programaId));
    }

    @GetMapping("/candidatos-defesa")
    @Operation(summary = "Listar candidatos a defesa", description = "Retorna discentes que ainda não defenderam")
    public ResponseEntity<List<DiscenteResponse>> listarCandidatosDefesa() {
        log.info("Listando discentes candidatos a defesa");
        return ResponseEntity.ok(discenteService.findDiscentesCandidatosDefesa());
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar discentes por nome", description = "Busca discentes por nome (parcial)")
    public ResponseEntity<PagedResponse<DiscenteResponse>> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome") @RequestParam String nome,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Buscando discentes por nome: {}", nome);
        return ResponseEntity.ok(PagedResponse.of(discenteService.searchByNome(nome, pageable)));
    }

    @PostMapping
    @Operation(summary = "Criar novo discente", description = "Cria um novo discente no sistema")
    public ResponseEntity<DiscenteResponse> criar(
            @Valid @RequestBody DiscenteCreateRequest request) {
        log.info("Criando novo discente: {}", request.getNome());
        DiscenteResponse created = discenteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar discente", description = "Atualiza os dados de um discente existente")
    public ResponseEntity<DiscenteResponse> atualizar(
            @Parameter(description = "ID do discente") @PathVariable Long id,
            @Valid @RequestBody DiscenteUpdateRequest request) {
        log.info("Atualizando discente ID: {}", id);
        return discenteService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar discente", description = "Remove um discente do sistema")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do discente") @PathVariable Long id) {
        log.info("Deletando discente ID: {}", id);
        return discenteService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
