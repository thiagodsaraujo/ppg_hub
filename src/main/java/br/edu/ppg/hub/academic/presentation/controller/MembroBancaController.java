package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.membro_banca.MembroBancaResponseDTO;
import br.edu.ppg.hub.academic.application.service.MembroBancaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller REST para gerenciamento de Membros de Banca.
 * Fornece endpoints para gestão de participação em bancas examinadoras.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/membros-banca")
@RequiredArgsConstructor
@Tag(name = "Membros de Banca", description = "Endpoints para gerenciamento de membros de bancas examinadoras")
@SecurityRequirement(name = "bearer-jwt")
public class MembroBancaController {

    private final MembroBancaService membroBancaService;

    /**
     * Busca membro por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar membro por ID", description = "Retorna dados de um membro de banca")
    public ResponseEntity<MembroBancaResponseDTO> buscarPorId(@PathVariable Long id) {
        MembroBancaResponseDTO response = membroBancaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca todos os membros de uma banca.
     */
    @GetMapping("/banca/{bancaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar membros por banca", description = "Retorna todos os membros de uma banca")
    public ResponseEntity<List<MembroBancaResponseDTO>> buscarPorBanca(@PathVariable Long bancaId) {
        List<MembroBancaResponseDTO> response = membroBancaService.buscarPorBanca(bancaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca membros externos de uma banca.
     */
    @GetMapping("/banca/{bancaId}/externos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar membros externos", description = "Retorna membros externos de uma banca")
    public ResponseEntity<List<MembroBancaResponseDTO>> buscarMembrosExternos(@PathVariable Long bancaId) {
        List<MembroBancaResponseDTO> response = membroBancaService.buscarMembrosExternos(bancaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca membros internos de uma banca.
     */
    @GetMapping("/banca/{bancaId}/internos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar membros internos", description = "Retorna membros internos de uma banca")
    public ResponseEntity<List<MembroBancaResponseDTO>> buscarMembrosInternos(@PathVariable Long bancaId) {
        List<MembroBancaResponseDTO> response = membroBancaService.buscarMembrosInternos(bancaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca presidente de uma banca.
     */
    @GetMapping("/banca/{bancaId}/presidente")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar presidente", description = "Retorna o presidente de uma banca")
    public ResponseEntity<MembroBancaResponseDTO> buscarPresidente(@PathVariable Long bancaId) {
        MembroBancaResponseDTO response = membroBancaService.buscarPresidente(bancaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca membros que confirmaram presença.
     */
    @GetMapping("/banca/{bancaId}/confirmados")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar membros confirmados", description = "Retorna membros que confirmaram presença")
    public ResponseEntity<List<MembroBancaResponseDTO>> buscarMembrosConfirmados(@PathVariable Long bancaId) {
        List<MembroBancaResponseDTO> response = membroBancaService.buscarMembrosConfirmados(bancaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca membros que não confirmaram presença.
     */
    @GetMapping("/banca/{bancaId}/nao-confirmados")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar membros não confirmados", description = "Retorna membros que não confirmaram presença")
    public ResponseEntity<List<MembroBancaResponseDTO>> buscarMembrosNaoConfirmados(@PathVariable Long bancaId) {
        List<MembroBancaResponseDTO> response = membroBancaService.buscarMembrosNaoConfirmados(bancaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca participações de um docente em bancas.
     */
    @GetMapping("/docente/{docenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar participações por docente", description = "Retorna todas as participações de um docente em bancas")
    public ResponseEntity<Page<MembroBancaResponseDTO>> buscarPorDocente(
            @PathVariable Long docenteId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<MembroBancaResponseDTO> response = membroBancaService.buscarPorDocente(docenteId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca bancas onde o docente foi presidente.
     */
    @GetMapping("/docente/{docenteId}/presidente")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar bancas como presidente", description = "Retorna bancas onde o docente foi presidente")
    public ResponseEntity<List<MembroBancaResponseDTO>> buscarBancasComoPresidente(@PathVariable Long docenteId) {
        List<MembroBancaResponseDTO> response = membroBancaService.buscarBancasComoPresidente(docenteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Atribui nota e parecer de um membro.
     */
    @PostMapping("/{id}/atribuir-nota")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Atribuir nota", description = "Registra nota e parecer de um membro da banca")
    public ResponseEntity<MembroBancaResponseDTO> atribuirNota(
            @PathVariable Long id,
            @RequestParam BigDecimal nota,
            @RequestParam String parecer
    ) {
        MembroBancaResponseDTO response = membroBancaService.atribuirNota(id, nota, parecer);
        return ResponseEntity.ok(response);
    }

    /**
     * Faz upload do arquivo de parecer de um membro.
     */
    @PostMapping("/{id}/upload-parecer")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Upload de parecer", description = "Envia arquivo de parecer de um membro")
    public ResponseEntity<MembroBancaResponseDTO> uploadParecer(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        MembroBancaResponseDTO response = membroBancaService.uploadParecer(id, arquivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Confirma presença de um membro na banca.
     */
    @PostMapping("/{id}/confirmar-presenca")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Confirmar presença", description = "Membro confirma participação na banca")
    public ResponseEntity<MembroBancaResponseDTO> confirmarPresenca(@PathVariable Long id) {
        MembroBancaResponseDTO response = membroBancaService.confirmarPresenca(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra presença de um membro na banca.
     */
    @PostMapping("/{id}/registrar-presenca")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Registrar presença", description = "Registra presença efetiva do membro na banca")
    public ResponseEntity<MembroBancaResponseDTO> registrarPresenca(@PathVariable Long id) {
        MembroBancaResponseDTO response = membroBancaService.registrarPresenca(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra ausência de um membro com justificativa.
     */
    @PostMapping("/{id}/registrar-ausencia")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Registrar ausência", description = "Registra ausência do membro com justificativa")
    public ResponseEntity<MembroBancaResponseDTO> registrarAusencia(
            @PathVariable Long id,
            @RequestParam String justificativa
    ) {
        MembroBancaResponseDTO response = membroBancaService.registrarAusencia(id, justificativa);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza ordem de apresentação de um membro.
     */
    @PutMapping("/{id}/ordem")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualizar ordem", description = "Altera ordem de apresentação do membro na banca")
    public ResponseEntity<MembroBancaResponseDTO> atualizarOrdem(
            @PathVariable Long id,
            @RequestParam Integer ordem
    ) {
        MembroBancaResponseDTO response = membroBancaService.atualizarOrdem(id, ordem);
        return ResponseEntity.ok(response);
    }

    /**
     * Deleta um membro de banca.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Deletar membro", description = "Remove um membro da banca")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        membroBancaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
