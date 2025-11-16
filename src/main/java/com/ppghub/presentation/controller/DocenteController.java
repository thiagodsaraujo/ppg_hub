package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.DocenteCreateRequest;
import com.ppghub.application.dto.response.DocenteResponse;
import com.ppghub.domain.service.DocenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Docentes/Pesquisadores.
 */
@RestController
@RequestMapping("/v1/docentes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Docentes", description = "Endpoints para gerenciamento de docentes e pesquisadores")
public class DocenteController {

    private final DocenteService docenteService;

    @GetMapping
    @Operation(summary = "Listar todos os docentes", description = "Retorna lista de todos os docentes")
    public ResponseEntity<List<DocenteResponse>> listarTodos() {
        log.info("Listando todos os docentes");
        return ResponseEntity.ok(docenteService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar docente por ID", description = "Retorna um docente específico pelo ID")
    public ResponseEntity<DocenteResponse> buscarPorId(
            @Parameter(description = "ID do docente") @PathVariable Long id) {
        log.info("Buscando docente por ID: {}", id);
        return docenteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar docente por CPF", description = "Retorna docente pelo CPF")
    public ResponseEntity<DocenteResponse> buscarPorCpf(
            @Parameter(description = "CPF do docente") @PathVariable String cpf) {
        log.info("Buscando docente por CPF");
        return docenteService.findByCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lattes/{lattesId}")
    @Operation(summary = "Buscar docente por Lattes ID", description = "Retorna docente pelo ID Lattes")
    public ResponseEntity<DocenteResponse> buscarPorLattesId(
            @Parameter(description = "Lattes ID") @PathVariable String lattesId) {
        log.info("Buscando docente por Lattes ID: {}", lattesId);
        return docenteService.findByLattesId(lattesId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orcid/{orcid}")
    @Operation(summary = "Buscar docente por ORCID", description = "Retorna docente pelo ORCID")
    public ResponseEntity<DocenteResponse> buscarPorOrcid(
            @Parameter(description = "ORCID") @PathVariable String orcid) {
        log.info("Buscando docente por ORCID: {}", orcid);
        return docenteService.findByOrcid(orcid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/openalex/{openalexId}")
    @Operation(summary = "Buscar docente por OpenAlex Author ID", description = "Retorna docente pelo ID OpenAlex")
    public ResponseEntity<DocenteResponse> buscarPorOpenAlexId(
            @Parameter(description = "OpenAlex Author ID") @PathVariable String openalexId) {
        log.info("Buscando docente por OpenAlex ID: {}", openalexId);
        return docenteService.findByOpenalexAuthorId(openalexId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instituicao/{instituicaoId}")
    @Operation(summary = "Listar docentes por instituição", description = "Retorna docentes de uma instituição")
    public ResponseEntity<List<DocenteResponse>> listarPorInstituicao(
            @Parameter(description = "ID da instituição") @PathVariable Long instituicaoId) {
        log.info("Listando docentes da instituição ID: {}", instituicaoId);
        return ResponseEntity.ok(docenteService.findByInstituicao(instituicaoId));
    }

    @GetMapping("/instituicao/{instituicaoId}/ativos")
    @Operation(summary = "Listar docentes ativos por instituição", description = "Retorna docentes ativos de uma instituição")
    public ResponseEntity<List<DocenteResponse>> listarAtivosPorInstituicao(
            @Parameter(description = "ID da instituição") @PathVariable Long instituicaoId) {
        log.info("Listando docentes ativos da instituição ID: {}", instituicaoId);
        return ResponseEntity.ok(docenteService.findAtivosByInstituicao(instituicaoId));
    }

    @GetMapping("/sync/pending")
    @Operation(summary = "Listar docentes que precisam de sincronização",
               description = "Retorna docentes que precisam ser sincronizados com OpenAlex")
    public ResponseEntity<List<DocenteResponse>> listarDocentesParaSync(
            @Parameter(description = "Threshold em dias", required = false)
            @RequestParam(defaultValue = "30") int daysThreshold) {
        log.info("Listando docentes que precisam de sincronização (threshold: {} dias)", daysThreshold);
        return ResponseEntity.ok(docenteService.findDocentesNeedingSync(daysThreshold));
    }

    @GetMapping("/sem-openalex")
    @Operation(summary = "Listar docentes sem OpenAlex ID", description = "Retorna docentes que não têm ID do OpenAlex")
    public ResponseEntity<List<DocenteResponse>> listarDocentesSemOpenAlexId() {
        log.info("Listando docentes sem OpenAlex ID");
        return ResponseEntity.ok(docenteService.findDocentesSemOpenAlexId());
    }

    @PostMapping
    @Operation(summary = "Criar novo docente", description = "Cria um novo docente no sistema")
    public ResponseEntity<DocenteResponse> criar(
            @Valid @RequestBody DocenteCreateRequest request) {
        log.info("Criando novo docente: {}", request.getNomeCompleto());
        DocenteResponse created = docenteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar docente", description = "Atualiza os dados de um docente existente")
    public ResponseEntity<DocenteResponse> atualizar(
            @Parameter(description = "ID do docente") @PathVariable Long id,
            @Valid @RequestBody DocenteCreateRequest request) {
        log.info("Atualizando docente ID: {}", id);
        return docenteService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar docente", description = "Remove um docente do sistema")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do docente") @PathVariable Long id) {
        log.info("Deletando docente ID: {}", id);
        return docenteService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar docente", description = "Marca um docente como inativo")
    public ResponseEntity<DocenteResponse> desativar(
            @Parameter(description = "ID do docente") @PathVariable Long id) {
        log.info("Desativando docente ID: {}", id);
        return docenteService.desativar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
