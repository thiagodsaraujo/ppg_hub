package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.InstituicaoCreateRequest;
import com.ppghub.application.dto.response.InstituicaoResponse;
import com.ppghub.domain.service.InstituicaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Instituições.
 */
@RestController
@RequestMapping("/v1/instituicoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Instituições", description = "Endpoints para gerenciamento de instituições de ensino superior")
public class InstituicaoController {

    private final InstituicaoService instituicaoService;

    @GetMapping
    @Operation(summary = "Listar todas as instituições", description = "Retorna lista de todas as instituições")
    public ResponseEntity<List<InstituicaoResponse>> listarTodas() {
        log.info("Listando todas as instituições");
        return ResponseEntity.ok(instituicaoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar instituição por ID", description = "Retorna uma instituição específica pelo ID")
    public ResponseEntity<InstituicaoResponse> buscarPorId(
            @Parameter(description = "ID da instituição") @PathVariable Long id) {
        log.info("Buscando instituição por ID: {}", id);
        return instituicaoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sigla/{sigla}")
    @Operation(summary = "Buscar instituição por sigla", description = "Retorna instituição pela sigla")
    public ResponseEntity<InstituicaoResponse> buscarPorSigla(
            @Parameter(description = "Sigla da instituição") @PathVariable String sigla) {
        log.info("Buscando instituição por sigla: {}", sigla);
        return instituicaoService.findBySigla(sigla)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/openalex/{openalexId}")
    @Operation(summary = "Buscar instituição por OpenAlex ID", description = "Retorna instituição pelo ID do OpenAlex")
    public ResponseEntity<InstituicaoResponse> buscarPorOpenAlexId(
            @Parameter(description = "OpenAlex Institution ID") @PathVariable String openalexId) {
        log.info("Buscando instituição por OpenAlex ID: {}", openalexId);
        return instituicaoService.findByOpenalexId(openalexId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ror/{rorId}")
    @Operation(summary = "Buscar instituição por ROR ID", description = "Retorna instituição pelo Research Organization Registry ID")
    public ResponseEntity<InstituicaoResponse> buscarPorRorId(
            @Parameter(description = "ROR ID") @PathVariable String rorId) {
        log.info("Buscando instituição por ROR ID: {}", rorId);
        return instituicaoService.findByRorId(rorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar instituições por nome", description = "Busca instituições que contenham o termo no nome")
    public ResponseEntity<Page<InstituicaoResponse>> buscarPorNome(
            @Parameter(description = "Nome da instituição") @RequestParam String nome,
            Pageable pageable) {
        log.info("Buscando instituições por nome: {}", nome);
        return ResponseEntity.ok(instituicaoService.searchByNome(nome, pageable));
    }

    @GetMapping("/ativas")
    @Operation(summary = "Listar instituições ativas", description = "Retorna apenas instituições ativas")
    public ResponseEntity<List<InstituicaoResponse>> listarAtivas() {
        log.info("Listando instituições ativas");
        return ResponseEntity.ok(instituicaoService.findAtivas());
    }

    @PostMapping
    @Operation(summary = "Criar nova instituição", description = "Cria uma nova instituição no sistema")
    public ResponseEntity<InstituicaoResponse> criar(
            @Valid @RequestBody InstituicaoCreateRequest request) {
        log.info("Criando nova instituição: {}", request.getNome());
        InstituicaoResponse created = instituicaoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar instituição", description = "Atualiza os dados de uma instituição existente")
    public ResponseEntity<InstituicaoResponse> atualizar(
            @Parameter(description = "ID da instituição") @PathVariable Long id,
            @Valid @RequestBody InstituicaoCreateRequest request) {
        log.info("Atualizando instituição ID: {}", id);
        return instituicaoService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar instituição", description = "Remove uma instituição do sistema")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da instituição") @PathVariable Long id) {
        log.info("Deletando instituição ID: {}", id);
        return instituicaoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar instituição", description = "Marca uma instituição como inativa (soft delete)")
    public ResponseEntity<InstituicaoResponse> desativar(
            @Parameter(description = "ID da instituição") @PathVariable Long id) {
        log.info("Desativando instituição ID: {}", id);
        return instituicaoService.desativar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
