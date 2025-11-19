package br.edu.ppg.hub.core.presentation.controller;

import br.edu.ppg.hub.core.application.dto.linha_pesquisa.LinhaPesquisaCreateDTO;
import br.edu.ppg.hub.core.application.dto.linha_pesquisa.LinhaPesquisaResponseDTO;
import br.edu.ppg.hub.core.application.dto.linha_pesquisa.LinhaPesquisaUpdateDTO;
import br.edu.ppg.hub.core.application.service.LinhaPesquisaService;
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

import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciamento de Linhas de Pesquisa.
 */
@RestController
@RequestMapping("/api/v1/linhas-pesquisa")
@RequiredArgsConstructor
@Tag(name = "Linhas de Pesquisa", description = "Endpoints para gerenciamento de linhas de pesquisa")
@SecurityRequirement(name = "bearerAuth")
public class LinhaPesquisaController {

    private final LinhaPesquisaService linhaPesquisaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE')")
    @Operation(summary = "Lista todas as linhas de pesquisa com paginação")
    public ResponseEntity<Page<LinhaPesquisaResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(linhaPesquisaService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Busca linha de pesquisa por ID")
    public ResponseEntity<LinhaPesquisaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(linhaPesquisaService.findById(id));
    }

    @GetMapping("/programa/{programaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Lista linhas de pesquisa por programa")
    public ResponseEntity<Page<LinhaPesquisaResponseDTO>> findByPrograma(
            @PathVariable Long programaId,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        return ResponseEntity.ok(linhaPesquisaService.findByPrograma(programaId, pageable));
    }

    @GetMapping("/programa/{programaId}/ativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Lista linhas de pesquisa ativas por programa")
    public ResponseEntity<List<LinhaPesquisaResponseDTO>> findAtivasByPrograma(@PathVariable Long programaId) {
        return ResponseEntity.ok(linhaPesquisaService.findAtivasByPrograma(programaId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA', 'DOCENTE')")
    @Operation(summary = "Busca linhas de pesquisa por nome (busca parcial)")
    public ResponseEntity<Page<LinhaPesquisaResponseDTO>> search(
            @RequestParam String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        return ResponseEntity.ok(linhaPesquisaService.findByNome(nome, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Cria nova linha de pesquisa")
    public ResponseEntity<LinhaPesquisaResponseDTO> create(@Valid @RequestBody LinhaPesquisaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(linhaPesquisaService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualiza linha de pesquisa existente")
    public ResponseEntity<LinhaPesquisaResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody LinhaPesquisaUpdateDTO dto
    ) {
        return ResponseEntity.ok(linhaPesquisaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deleta linha de pesquisa")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        linhaPesquisaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Ativa linha de pesquisa")
    public ResponseEntity<LinhaPesquisaResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(linhaPesquisaService.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Desativa linha de pesquisa")
    public ResponseEntity<LinhaPesquisaResponseDTO> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(linhaPesquisaService.desativar(id));
    }

    @GetMapping("/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Retorna estatísticas gerais de linhas de pesquisa")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        return ResponseEntity.ok(linhaPesquisaService.getEstatisticas());
    }

    @GetMapping("/estatisticas/programa/{programaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Retorna estatísticas de um programa específico")
    public ResponseEntity<Map<String, Object>> getEstatisticasPorPrograma(@PathVariable Long programaId) {
        return ResponseEntity.ok(linhaPesquisaService.getEstatisticasPorPrograma(programaId));
    }
}
