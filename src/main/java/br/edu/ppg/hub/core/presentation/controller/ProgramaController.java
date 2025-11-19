package br.edu.ppg.hub.core.presentation.controller;

import br.edu.ppg.hub.core.application.dto.programa.ProgramaCreateDTO;
import br.edu.ppg.hub.core.application.dto.programa.ProgramaResponseDTO;
import br.edu.ppg.hub.core.application.dto.programa.ProgramaUpdateDTO;
import br.edu.ppg.hub.core.application.service.ProgramaService;
import br.edu.ppg.hub.core.domain.enums.StatusPrograma;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para gerenciamento de Programas de Pós-Graduação.
 */
@RestController
@RequestMapping("/api/v1/programas")
@RequiredArgsConstructor
@Tag(name = "Programas", description = "Endpoints para gerenciamento de programas de pós-graduação")
@SecurityRequirement(name = "bearerAuth")
public class ProgramaController {

    private final ProgramaService programaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE')")
    @Operation(summary = "Lista todos os programas com paginação")
    public ResponseEntity<Page<ProgramaResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(programaService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Busca programa por ID")
    public ResponseEntity<ProgramaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(programaService.findById(id));
    }

    @GetMapping("/codigo-capes/{codigoCapes}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Busca programa por código CAPES")
    public ResponseEntity<ProgramaResponseDTO> findByCodigoCapes(@PathVariable String codigoCapes) {
        return ResponseEntity.ok(programaService.findByCodigoCapes(codigoCapes));
    }

    @GetMapping("/instituicao/{instituicaoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE')")
    @Operation(summary = "Lista programas por instituição")
    public ResponseEntity<Page<ProgramaResponseDTO>> findByInstituicao(
            @PathVariable Long instituicaoId,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        return ResponseEntity.ok(programaService.findByInstituicao(instituicaoId, pageable));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista programas por status")
    public ResponseEntity<Page<ProgramaResponseDTO>> findByStatus(
            @PathVariable StatusPrograma status,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        return ResponseEntity.ok(programaService.findByStatus(status, pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE')")
    @Operation(summary = "Busca programas por nome (busca parcial)")
    public ResponseEntity<Page<ProgramaResponseDTO>> search(
            @RequestParam String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        return ResponseEntity.ok(programaService.findByNome(nome, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Cria novo programa")
    public ResponseEntity<ProgramaResponseDTO> create(@Valid @RequestBody ProgramaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(programaService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualiza programa existente")
    public ResponseEntity<ProgramaResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgramaUpdateDTO dto
    ) {
        return ResponseEntity.ok(programaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deleta programa")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        programaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Ativa programa")
    public ResponseEntity<ProgramaResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(programaService.ativar(id));
    }

    @PatchMapping("/{id}/suspender")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Suspende programa")
    public ResponseEntity<ProgramaResponseDTO> suspender(@PathVariable Long id) {
        return ResponseEntity.ok(programaService.suspender(id));
    }

    @GetMapping("/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Retorna estatísticas gerais dos programas")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        return ResponseEntity.ok(programaService.getEstatisticas());
    }

    @GetMapping("/estatisticas/instituicao/{instituicaoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Retorna estatísticas de uma instituição específica")
    public ResponseEntity<Map<String, Object>> getEstatisticasPorInstituicao(@PathVariable Long instituicaoId) {
        return ResponseEntity.ok(programaService.getEstatisticasPorInstituicao(instituicaoId));
    }
}
