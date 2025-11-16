package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.MembroBancaCreateRequest;
import com.ppghub.application.dto.response.MembroBancaResponse;
import com.ppghub.domain.service.MembroBancaService;
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
 * Controller REST para gerenciamento de Membros de Banca.
 */
@RestController
@RequestMapping("/v1/membros-banca")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Membros de Banca", description = "Endpoints para gerenciamento de membros de bancas de defesa")
public class MembroBancaController {

    private final MembroBancaService membroBancaService;

    @GetMapping
    @Operation(summary = "Listar todos os membros de banca", description = "Retorna lista de todos os membros de banca")
    public ResponseEntity<List<MembroBancaResponse>> listarTodos() {
        log.info("Listando todos os membros de banca");
        return ResponseEntity.ok(membroBancaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar membro de banca por ID", description = "Retorna um membro de banca específico pelo ID")
    public ResponseEntity<MembroBancaResponse> buscarPorId(
            @Parameter(description = "ID do membro de banca") @PathVariable Long id) {
        log.info("Buscando membro de banca por ID: {}", id);
        return membroBancaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/banca/{bancaId}")
    @Operation(summary = "Listar membros de uma banca", description = "Retorna membros de uma banca ordenados por ordem de apresentação")
    public ResponseEntity<List<MembroBancaResponse>> listarPorBanca(
            @Parameter(description = "ID da banca") @PathVariable Long bancaId) {
        log.info("Listando membros da banca ID: {}", bancaId);
        return ResponseEntity.ok(membroBancaService.findByBanca(bancaId));
    }

    @GetMapping("/banca/{bancaId}/titulares")
    @Operation(summary = "Listar membros titulares de uma banca", description = "Retorna apenas membros titulares de uma banca")
    public ResponseEntity<List<MembroBancaResponse>> listarTitularesPorBanca(
            @Parameter(description = "ID da banca") @PathVariable Long bancaId) {
        log.info("Listando membros titulares da banca ID: {}", bancaId);
        return ResponseEntity.ok(membroBancaService.findTitularesByBanca(bancaId));
    }

    @GetMapping("/banca/{bancaId}/suplentes")
    @Operation(summary = "Listar membros suplentes de uma banca", description = "Retorna apenas membros suplentes de uma banca")
    public ResponseEntity<List<MembroBancaResponse>> listarSuplentesPorBanca(
            @Parameter(description = "ID da banca") @PathVariable Long bancaId) {
        log.info("Listando membros suplentes da banca ID: {}", bancaId);
        return ResponseEntity.ok(membroBancaService.findSuplentesByBanca(bancaId));
    }

    @GetMapping("/docente/{docenteId}")
    @Operation(summary = "Listar participações de um docente", description = "Retorna bancas em que um docente participa/participou")
    public ResponseEntity<List<MembroBancaResponse>> listarPorDocente(
            @Parameter(description = "ID do docente") @PathVariable Long docenteId) {
        log.info("Listando participações do docente ID: {}", docenteId);
        return ResponseEntity.ok(membroBancaService.findByDocente(docenteId));
    }

    @GetMapping("/professor-externo/{professorExternoId}")
    @Operation(summary = "Listar participações de um professor externo", description = "Retorna bancas em que um professor externo participa/participou")
    public ResponseEntity<List<MembroBancaResponse>> listarPorProfessorExterno(
            @Parameter(description = "ID do professor externo") @PathVariable Long professorExternoId) {
        log.info("Listando participações do professor externo ID: {}", professorExternoId);
        return ResponseEntity.ok(membroBancaService.findByProfessorExterno(professorExternoId));
    }

    @PostMapping("/banca/{bancaId}")
    @Operation(summary = "Adicionar membro à banca", description = "Adiciona um novo membro (docente ou professor externo) a uma banca")
    public ResponseEntity<MembroBancaResponse> adicionarMembro(
            @Parameter(description = "ID da banca") @PathVariable Long bancaId,
            @Valid @RequestBody MembroBancaCreateRequest request) {
        log.info("Adicionando membro à banca ID: {}", bancaId);
        MembroBancaResponse created = membroBancaService.addMembro(bancaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar participação", description = "Confirma a participação de um membro na banca")
    public ResponseEntity<MembroBancaResponse> confirmarParticipacao(
            @Parameter(description = "ID do membro de banca") @PathVariable Long id) {
        log.info("Confirmando participação do membro ID: {}", id);
        return membroBancaService.confirmarParticipacao(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/recusar")
    @Operation(summary = "Recusar participação", description = "Recusa a participação de um membro na banca")
    public ResponseEntity<MembroBancaResponse> recusarParticipacao(
            @Parameter(description = "ID do membro de banca") @PathVariable Long id,
            @Parameter(description = "Motivo da recusa") @RequestParam(required = false) String motivo) {
        log.info("Recusando participação do membro ID: {}", id);
        return membroBancaService.recusarParticipacao(id, motivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/enviar-convite")
    @Operation(summary = "Enviar convite", description = "Envia convite para um membro de banca")
    public ResponseEntity<MembroBancaResponse> enviarConvite(
            @Parameter(description = "ID do membro de banca") @PathVariable Long id) {
        log.info("Enviando convite para membro ID: {}", id);
        return membroBancaService.enviarConvite(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover membro da banca", description = "Remove um membro de uma banca (não permite bancas já realizadas)")
    public ResponseEntity<Void> removerMembro(
            @Parameter(description = "ID do membro de banca") @PathVariable Long id) {
        log.info("Removendo membro de banca ID: {}", id);
        return membroBancaService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/banca/{bancaId}/membro/{membroId}")
    @Operation(summary = "Remover membro específico da banca", description = "Remove um membro específico de uma banca")
    public ResponseEntity<Void> removerMembroDaBanca(
            @Parameter(description = "ID da banca") @PathVariable Long bancaId,
            @Parameter(description = "ID do membro") @PathVariable Long membroId) {
        log.info("Removendo membro ID: {} da banca ID: {}", membroId, bancaId);
        return membroBancaService.removeMembro(bancaId, membroId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
