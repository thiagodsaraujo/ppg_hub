package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.PublicacaoCreateRequest;
import com.ppghub.application.dto.response.PublicacaoResponse;
import com.ppghub.domain.service.PublicacaoService;
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
 * Controller REST para gerenciamento de Publicações científicas.
 */
@RestController
@RequestMapping("/v1/publicacoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Publicações", description = "Endpoints para gerenciamento de publicações científicas")
public class PublicacaoController {

    private final PublicacaoService publicacaoService;

    @GetMapping
    @Operation(summary = "Listar todas as publicações", description = "Retorna lista de todas as publicações")
    public ResponseEntity<List<PublicacaoResponse>> listarTodas() {
        log.info("Listando todas as publicações");
        return ResponseEntity.ok(publicacaoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar publicação por ID", description = "Retorna uma publicação específica pelo ID")
    public ResponseEntity<PublicacaoResponse> buscarPorId(
            @Parameter(description = "ID da publicação") @PathVariable Long id) {
        log.info("Buscando publicação por ID: {}", id);
        return publicacaoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/openalex/{openalexWorkId}")
    @Operation(summary = "Buscar publicação por OpenAlex Work ID", description = "Retorna publicação pelo ID OpenAlex")
    public ResponseEntity<PublicacaoResponse> buscarPorOpenAlexWorkId(
            @Parameter(description = "OpenAlex Work ID") @PathVariable String openalexWorkId) {
        log.info("Buscando publicação por OpenAlex Work ID: {}", openalexWorkId);
        return publicacaoService.findByOpenalexWorkId(openalexWorkId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doi/{doi}")
    @Operation(summary = "Buscar publicação por DOI", description = "Retorna publicação pelo DOI")
    public ResponseEntity<PublicacaoResponse> buscarPorDoi(
            @Parameter(description = "DOI") @PathVariable String doi) {
        log.info("Buscando publicação por DOI: {}", doi);
        // DOI pode conter '/', então precisa ser URL encoded
        return publicacaoService.findByDoi(doi)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ano/{ano}")
    @Operation(summary = "Buscar publicações por ano", description = "Retorna publicações de um ano específico")
    public ResponseEntity<List<PublicacaoResponse>> buscarPorAno(
            @Parameter(description = "Ano de publicação") @PathVariable Integer ano) {
        log.info("Buscando publicações do ano: {}", ano);
        return ResponseEntity.ok(publicacaoService.findByAno(ano));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar publicações por tipo", description = "Retorna publicações de um tipo específico")
    public ResponseEntity<List<PublicacaoResponse>> buscarPorTipo(
            @Parameter(description = "Tipo de publicação (article, book-chapter, etc)") @PathVariable String tipo) {
        log.info("Buscando publicações por tipo: {}", tipo);
        return ResponseEntity.ok(publicacaoService.findByTipo(tipo));
    }

    @GetMapping("/instituicao/{instituicaoId}")
    @Operation(summary = "Listar publicações por instituição", description = "Retorna publicações de uma instituição (paginado)")
    public ResponseEntity<Page<PublicacaoResponse>> listarPorInstituicao(
            @Parameter(description = "ID da instituição") @PathVariable Long instituicaoId,
            Pageable pageable) {
        log.info("Listando publicações da instituição ID: {}", instituicaoId);
        return ResponseEntity.ok(publicacaoService.findByInstituicao(instituicaoId, pageable));
    }

    @GetMapping("/docente/{docenteId}")
    @Operation(summary = "Listar publicações por docente", description = "Retorna publicações de um docente")
    public ResponseEntity<List<PublicacaoResponse>> listarPorDocente(
            @Parameter(description = "ID do docente") @PathVariable Long docenteId) {
        log.info("Listando publicações do docente ID: {}", docenteId);
        return ResponseEntity.ok(publicacaoService.findByDocente(docenteId));
    }

    @GetMapping("/mais-citadas")
    @Operation(summary = "Listar publicações mais citadas", description = "Retorna publicações ordenadas por citações")
    public ResponseEntity<Page<PublicacaoResponse>> listarMaisCitadas(
            Pageable pageable) {
        log.info("Listando publicações mais citadas");
        return ResponseEntity.ok(publicacaoService.findMaisCitadas(pageable));
    }

    @PostMapping
    @Operation(summary = "Criar nova publicação", description = "Cria uma nova publicação no sistema")
    public ResponseEntity<PublicacaoResponse> criar(
            @Valid @RequestBody PublicacaoCreateRequest request) {
        log.info("Criando nova publicação: {}", request.getTitulo());
        PublicacaoResponse created = publicacaoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar publicação", description = "Atualiza os dados de uma publicação existente")
    public ResponseEntity<PublicacaoResponse> atualizar(
            @Parameter(description = "ID da publicação") @PathVariable Long id,
            @Valid @RequestBody PublicacaoCreateRequest request) {
        log.info("Atualizando publicação ID: {}", id);
        return publicacaoService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar publicação", description = "Remove uma publicação do sistema")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da publicação") @PathVariable Long id) {
        log.info("Deletando publicação ID: {}", id);
        return publicacaoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/exists/openalex/{openalexWorkId}")
    @Operation(summary = "Verificar se publicação existe por OpenAlex ID", description = "Verifica existência por OpenAlex Work ID")
    public ResponseEntity<Boolean> existsByOpenAlexWorkId(
            @Parameter(description = "OpenAlex Work ID") @PathVariable String openalexWorkId) {
        log.info("Verificando existência de publicação por OpenAlex Work ID: {}", openalexWorkId);
        return ResponseEntity.ok(publicacaoService.existsByOpenalexWorkId(openalexWorkId));
    }

    @GetMapping("/exists/doi/{doi}")
    @Operation(summary = "Verificar se publicação existe por DOI", description = "Verifica existência por DOI")
    public ResponseEntity<Boolean> existsByDoi(
            @Parameter(description = "DOI") @PathVariable String doi) {
        log.info("Verificando existência de publicação por DOI: {}", doi);
        return ResponseEntity.ok(publicacaoService.existsByDoi(doi));
    }
}
