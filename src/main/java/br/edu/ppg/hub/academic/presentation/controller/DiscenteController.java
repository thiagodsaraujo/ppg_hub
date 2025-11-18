package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.discente.DiscenteCreateDTO;
import br.edu.ppg.hub.academic.application.dto.discente.DiscenteResponseDTO;
import br.edu.ppg.hub.academic.application.dto.discente.DiscenteUpdateDTO;
import br.edu.ppg.hub.academic.application.service.DiscenteService;
import br.edu.ppg.hub.academic.domain.enums.StatusDiscente;
import br.edu.ppg.hub.academic.domain.enums.TipoCurso;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controller REST para gerenciamento de Discentes
 *
 * @author PPG Hub
 * @since 1.0
 */
@RestController
@RequestMapping("/api/discentes")
@RequiredArgsConstructor
@Tag(name = "Discentes", description = "Endpoints para gerenciamento de discentes (alunos de mestrado e doutorado)")
@SecurityRequirement(name = "bearer-jwt")
public class DiscenteController {

    private final DiscenteService discenteService;

    /**
     * Matricula um novo discente
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Matricular discente", description = "Realiza a matrícula de um novo discente no programa")
    public ResponseEntity<DiscenteResponseDTO> matricular(@Valid @RequestBody DiscenteCreateDTO dto) {
        DiscenteResponseDTO response = discenteService.matricular(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza um discente existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Atualizar discente", description = "Atualiza dados de um discente existente")
    public ResponseEntity<DiscenteResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DiscenteUpdateDTO dto
    ) {
        DiscenteResponseDTO response = discenteService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca discente por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar discente por ID", description = "Retorna dados completos de um discente")
    public ResponseEntity<DiscenteResponseDTO> buscarPorId(@PathVariable Long id) {
        DiscenteResponseDTO response = discenteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca todos os discentes (paginado)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar todos os discentes", description = "Retorna lista paginada de discentes")
    public ResponseEntity<Page<DiscenteResponseDTO>> buscarTodos(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DiscenteResponseDTO> response = discenteService.buscarTodos(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca discentes por programa
     */
    @GetMapping("/programa/{programaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar discentes por programa", description = "Retorna discentes de um programa específico")
    public ResponseEntity<Page<DiscenteResponseDTO>> buscarPorPrograma(
            @PathVariable Long programaId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DiscenteResponseDTO> response = discenteService.buscarPorPrograma(programaId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca discentes por orientador
     */
    @GetMapping("/orientador/{orientadorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar discentes por orientador", description = "Retorna orientandos de um docente")
    public ResponseEntity<Page<DiscenteResponseDTO>> buscarPorOrientador(
            @PathVariable Long orientadorId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DiscenteResponseDTO> response = discenteService.buscarPorOrientador(orientadorId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca discentes por status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar discentes por status", description = "Filtra discentes por status")
    public ResponseEntity<Page<DiscenteResponseDTO>> buscarPorStatus(
            @PathVariable StatusDiscente status,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DiscenteResponseDTO> response = discenteService.buscarPorStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca discentes por tipo de curso
     */
    @GetMapping("/tipo-curso/{tipoCurso}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar discentes por tipo de curso", description = "Filtra discentes por mestrado ou doutorado")
    public ResponseEntity<Page<DiscenteResponseDTO>> buscarPorTipoCurso(
            @PathVariable TipoCurso tipoCurso,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DiscenteResponseDTO> response = discenteService.buscarPorTipoCurso(tipoCurso, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Vincula orientador a um discente
     */
    @PatchMapping("/{discenteId}/vincular-orientador/{orientadorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Vincular orientador", description = "Vincula ou troca o orientador de um discente")
    public ResponseEntity<DiscenteResponseDTO> vincularOrientador(
            @PathVariable Long discenteId,
            @PathVariable Long orientadorId
    ) {
        DiscenteResponseDTO response = discenteService.vincularOrientador(discenteId, orientadorId);
        return ResponseEntity.ok(response);
    }

    /**
     * Troca o orientador de um discente
     */
    @PatchMapping("/{discenteId}/trocar-orientador/{novoOrientadorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Trocar orientador", description = "Troca o orientador de um discente")
    public ResponseEntity<DiscenteResponseDTO> trocarOrientador(
            @PathVariable Long discenteId,
            @PathVariable Long novoOrientadorId,
            @RequestParam String motivo
    ) {
        DiscenteResponseDTO response = discenteService.trocarOrientador(discenteId, novoOrientadorId, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra a qualificação de um discente
     */
    @PatchMapping("/{id}/qualificacao")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Registrar qualificação", description = "Registra a qualificação de um discente")
    public ResponseEntity<DiscenteResponseDTO> registrarQualificacao(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataQualificacao,
            @RequestParam String resultado
    ) {
        DiscenteResponseDTO response = discenteService.registrarQualificacao(id, dataQualificacao, resultado);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra a defesa de um discente
     */
    @PatchMapping("/{id}/defesa")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Registrar defesa", description = "Registra a defesa de dissertação/tese")
    public ResponseEntity<DiscenteResponseDTO> registrarDefesa(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDefesa,
            @RequestParam String resultado,
            @RequestParam String tituloFinal
    ) {
        DiscenteResponseDTO response = discenteService.registrarDefesa(id, dataDefesa, resultado, tituloFinal);
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona uma prorrogação de prazo
     */
    @PatchMapping("/{id}/prorrogar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Prorrogar prazo", description = "Adiciona prorrogação ao prazo do discente")
    public ResponseEntity<DiscenteResponseDTO> adicionarProrrogacao(
            @PathVariable Long id,
            @RequestParam int meses,
            @RequestParam String motivo
    ) {
        DiscenteResponseDTO response = discenteService.adicionarProrrogacao(id, meses, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Tranca matrícula do discente
     */
    @PatchMapping("/{id}/trancar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Trancar matrícula", description = "Tranca a matrícula de um discente")
    public ResponseEntity<DiscenteResponseDTO> trancar(
            @PathVariable Long id,
            @RequestParam String motivo
    ) {
        DiscenteResponseDTO response = discenteService.trancar(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Desliga um discente do programa
     */
    @PatchMapping("/{id}/desligar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Desligar discente", description = "Desliga discente do programa")
    public ResponseEntity<DiscenteResponseDTO> desligar(
            @PathVariable Long id,
            @RequestParam String motivo
    ) {
        DiscenteResponseDTO response = discenteService.desligar(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Titula um discente
     */
    @PatchMapping("/{id}/titular")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Titular discente", description = "Marca discente como titulado/concluído")
    public ResponseEntity<DiscenteResponseDTO> titular(@PathVariable Long id) {
        DiscenteResponseDTO response = discenteService.titular(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna estatísticas de discentes de um programa
     */
    @GetMapping("/programa/{programaId}/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Estatísticas de discentes", description = "Retorna estatísticas agregadas de discentes do programa")
    public ResponseEntity<Map<String, Object>> getEstatisticas(@PathVariable Long programaId) {
        Map<String, Object> stats = discenteService.getEstatisticasPorPrograma(programaId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Deleta um discente
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar discente", description = "Remove discente do sistema (use com cuidado)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        discenteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
