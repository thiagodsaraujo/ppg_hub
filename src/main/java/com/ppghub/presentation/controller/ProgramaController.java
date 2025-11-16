package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.ProgramaCreateRequest;
import com.ppghub.application.dto.response.ProgramaResponse;
import com.ppghub.domain.service.ProgramaService;
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
 * Controller REST para gerenciamento de Programas de Pós-Graduação.
 */
@RestController
@RequestMapping("/v1/programas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Programas", description = "Endpoints para gerenciamento de programas de pós-graduação")
public class ProgramaController {

    private final ProgramaService programaService;

    @GetMapping
    @Operation(summary = "Listar todos os programas", description = "Retorna lista de todos os programas")
    public ResponseEntity<List<ProgramaResponse>> listarTodos() {
        log.info("Listando todos os programas");
        return ResponseEntity.ok(programaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar programa por ID", description = "Retorna um programa específico pelo ID")
    public ResponseEntity<ProgramaResponse> buscarPorId(
            @Parameter(description = "ID do programa") @PathVariable Long id) {
        log.info("Buscando programa por ID: {}", id);
        return programaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo-capes/{codigo}")
    @Operation(summary = "Buscar programa por código CAPES", description = "Retorna programa pelo código CAPES")
    public ResponseEntity<ProgramaResponse> buscarPorCodigoCapes(
            @Parameter(description = "Código CAPES") @PathVariable String codigo) {
        log.info("Buscando programa por código CAPES: {}", codigo);
        return programaService.findByCodigoCapes(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instituicao/{instituicaoId}")
    @Operation(summary = "Listar programas por instituição", description = "Retorna programas de uma instituição")
    public ResponseEntity<List<ProgramaResponse>> listarPorInstituicao(
            @Parameter(description = "ID da instituição") @PathVariable Long instituicaoId) {
        log.info("Listando programas da instituição ID: {}", instituicaoId);
        return ResponseEntity.ok(programaService.findByInstituicao(instituicaoId));
    }

    @GetMapping("/instituicao/{instituicaoId}/ativos")
    @Operation(summary = "Listar programas ativos por instituição", description = "Retorna programas ativos de uma instituição")
    public ResponseEntity<List<ProgramaResponse>> listarAtivosPorInstituicao(
            @Parameter(description = "ID da instituição") @PathVariable Long instituicaoId) {
        log.info("Listando programas ativos da instituição ID: {}", instituicaoId);
        return ResponseEntity.ok(programaService.findProgramasAtivosByInstituicao(instituicaoId));
    }

    @GetMapping("/area-conhecimento/{area}")
    @Operation(summary = "Buscar programas por área de conhecimento", description = "Retorna programas de uma área")
    public ResponseEntity<List<ProgramaResponse>> buscarPorAreaConhecimento(
            @Parameter(description = "Área de conhecimento") @PathVariable String area) {
        log.info("Buscando programas por área de conhecimento: {}", area);
        return ResponseEntity.ok(programaService.findByAreaConhecimento(area));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar programas por status", description = "Retorna programas por status")
    public ResponseEntity<List<ProgramaResponse>> buscarPorStatus(
            @Parameter(description = "Status do programa") @PathVariable String status) {
        log.info("Buscando programas por status: {}", status);
        return ResponseEntity.ok(programaService.findByStatus(status));
    }

    @PostMapping
    @Operation(summary = "Criar novo programa", description = "Cria um novo programa de pós-graduação")
    public ResponseEntity<ProgramaResponse> criar(
            @Valid @RequestBody ProgramaCreateRequest request) {
        log.info("Criando novo programa: {}", request.getNome());
        ProgramaResponse created = programaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar programa", description = "Atualiza os dados de um programa existente")
    public ResponseEntity<ProgramaResponse> atualizar(
            @Parameter(description = "ID do programa") @PathVariable Long id,
            @Valid @RequestBody ProgramaCreateRequest request) {
        log.info("Atualizando programa ID: {}", id);
        return programaService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar programa", description = "Remove um programa do sistema")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do programa") @PathVariable Long id) {
        log.info("Deletando programa ID: {}", id);
        return programaService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar programa", description = "Marca um programa como inativo")
    public ResponseEntity<ProgramaResponse> inativar(
            @Parameter(description = "ID do programa") @PathVariable Long id) {
        log.info("Inativando programa ID: {}", id);
        return programaService.inativar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
