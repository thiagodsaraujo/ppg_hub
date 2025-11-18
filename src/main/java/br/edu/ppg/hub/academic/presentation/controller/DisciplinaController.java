package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.disciplina.DisciplinaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.disciplina.DisciplinaResponseDTO;
import br.edu.ppg.hub.academic.application.dto.disciplina.DisciplinaUpdateDTO;
import br.edu.ppg.hub.academic.application.service.DisciplinaService;
import br.edu.ppg.hub.academic.domain.enums.StatusDisciplina;
import br.edu.ppg.hub.academic.domain.enums.TipoDisciplina;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de Disciplinas
 *
 * @author PPG Hub
 * @since 1.0
 */
@RestController
@RequestMapping("/api/disciplinas")
@RequiredArgsConstructor
@Tag(name = "Disciplinas", description = "Endpoints para gerenciamento de disciplinas")
@SecurityRequirement(name = "bearer-jwt")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    /**
     * Cria uma nova disciplina
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Criar nova disciplina", description = "Cria uma nova disciplina em um programa")
    public ResponseEntity<DisciplinaResponseDTO> criar(@Valid @RequestBody DisciplinaCreateDTO dto) {
        DisciplinaResponseDTO response = disciplinaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza uma disciplina existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualizar disciplina", description = "Atualiza dados de uma disciplina existente")
    public ResponseEntity<DisciplinaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DisciplinaUpdateDTO dto
    ) {
        DisciplinaResponseDTO response = disciplinaService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca disciplina por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar disciplina por ID", description = "Retorna dados completos de uma disciplina")
    public ResponseEntity<DisciplinaResponseDTO> buscarPorId(@PathVariable Long id) {
        DisciplinaResponseDTO response = disciplinaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca todas as disciplinas (paginado)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar todas as disciplinas", description = "Retorna lista paginada de disciplinas")
    public ResponseEntity<Page<DisciplinaResponseDTO>> buscarTodas(
            @PageableDefault(size = 20, sort = "codigo") Pageable pageable
    ) {
        Page<DisciplinaResponseDTO> response = disciplinaService.buscarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca disciplinas por programa
     */
    @GetMapping("/programa/{programaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar disciplinas por programa", description = "Retorna disciplinas de um programa específico")
    public ResponseEntity<Page<DisciplinaResponseDTO>> buscarPorPrograma(
            @PathVariable Long programaId,
            @PageableDefault(size = 20, sort = "codigo") Pageable pageable
    ) {
        Page<DisciplinaResponseDTO> response = disciplinaService.buscarPorPrograma(programaId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca disciplinas ativas de um programa
     */
    @GetMapping("/programa/{programaId}/ativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar disciplinas ativas", description = "Retorna disciplinas ativas de um programa")
    public ResponseEntity<List<DisciplinaResponseDTO>> buscarAtivasPorPrograma(@PathVariable Long programaId) {
        List<DisciplinaResponseDTO> response = disciplinaService.buscarAtivasPorPrograma(programaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca disciplinas por tipo
     */
    @GetMapping("/programa/{programaId}/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar disciplinas por tipo", description = "Retorna disciplinas de um tipo específico")
    public ResponseEntity<Page<DisciplinaResponseDTO>> buscarPorTipo(
            @PathVariable Long programaId,
            @PathVariable TipoDisciplina tipo,
            @PageableDefault(size = 20, sort = "codigo") Pageable pageable
    ) {
        Page<DisciplinaResponseDTO> response = disciplinaService.buscarPorTipo(programaId, tipo, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca disciplinas por status
     */
    @GetMapping("/programa/{programaId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Listar disciplinas por status", description = "Retorna disciplinas com status específico")
    public ResponseEntity<Page<DisciplinaResponseDTO>> buscarPorStatus(
            @PathVariable Long programaId,
            @PathVariable StatusDisciplina status,
            @PageableDefault(size = 20, sort = "codigo") Pageable pageable
    ) {
        Page<DisciplinaResponseDTO> response = disciplinaService.buscarPorStatus(programaId, status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Ativa uma disciplina
     */
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Ativar disciplina", description = "Ativa uma disciplina inativa")
    public ResponseEntity<DisciplinaResponseDTO> ativar(@PathVariable Long id) {
        DisciplinaResponseDTO response = disciplinaService.ativar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Desativa uma disciplina
     */
    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Desativar disciplina", description = "Desativa uma disciplina ativa")
    public ResponseEntity<DisciplinaResponseDTO> desativar(@PathVariable Long id) {
        DisciplinaResponseDTO response = disciplinaService.desativar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Duplica uma disciplina para outro programa
     */
    @PostMapping("/{id}/duplicar/{novoProgramaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Duplicar disciplina", description = "Duplica uma disciplina para outro programa")
    public ResponseEntity<DisciplinaResponseDTO> duplicar(
            @PathVariable Long id,
            @PathVariable Long novoProgramaId
    ) {
        DisciplinaResponseDTO response = disciplinaService.duplicar(id, novoProgramaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Deleta uma disciplina
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar disciplina", description = "Remove uma disciplina do sistema")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        disciplinaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtém estatísticas de disciplinas de um programa
     */
    @GetMapping("/programa/{programaId}/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Estatísticas de disciplinas", description = "Retorna estatísticas das disciplinas de um programa")
    public ResponseEntity<Map<String, Object>> getEstatisticas(@PathVariable Long programaId) {
        Map<String, Object> estatisticas = disciplinaService.getEstatisticas(programaId);
        return ResponseEntity.ok(estatisticas);
    }
}
