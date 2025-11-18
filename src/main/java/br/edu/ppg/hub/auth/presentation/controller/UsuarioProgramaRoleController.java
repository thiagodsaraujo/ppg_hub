package br.edu.ppg.hub.auth.presentation.controller;

import br.edu.ppg.hub.auth.application.dto.vinculacao.UsuarioProgramaRoleCreateDTO;
import br.edu.ppg.hub.auth.application.dto.vinculacao.UsuarioProgramaRoleResponseDTO;
import br.edu.ppg.hub.auth.application.dto.vinculacao.UsuarioProgramaRoleUpdateDTO;
import br.edu.ppg.hub.auth.application.service.UsuarioProgramaRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciamento de Vinculações de Usuários a Programas.
 */
@RestController
@RequestMapping("/api/v1/vinculacoes")
@RequiredArgsConstructor
@Tag(name = "Vinculações", description = "Endpoints para gerenciamento de vinculações usuário-programa-role")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioProgramaRoleController {

    private final UsuarioProgramaRoleService vinculacaoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista todas as vinculações com paginação")
    public ResponseEntity<Page<UsuarioProgramaRoleResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "dataVinculacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(vinculacaoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Busca vinculação por ID")
    public ResponseEntity<UsuarioProgramaRoleResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vinculacaoService.findById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista vinculações por usuário")
    public ResponseEntity<Page<UsuarioProgramaRoleResponseDTO>> findByUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 20, sort = "dataVinculacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(vinculacaoService.findByUsuario(usuarioId, pageable));
    }

    @GetMapping("/programa/{programaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista vinculações por programa")
    public ResponseEntity<Page<UsuarioProgramaRoleResponseDTO>> findByPrograma(
            @PathVariable Long programaId,
            @PageableDefault(size = 20, sort = "dataVinculacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(vinculacaoService.findByPrograma(programaId, pageable));
    }

    @GetMapping("/vigentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista vinculações vigentes de um usuário em um programa")
    public ResponseEntity<List<UsuarioProgramaRoleResponseDTO>> findVinculacoesVigentes(
            @RequestParam Long usuarioId,
            @RequestParam Long programaId
    ) {
        return ResponseEntity.ok(vinculacaoService.findVinculacoesVigentes(usuarioId, programaId));
    }

    @GetMapping("/verificar-role")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Verifica se usuário tem role específica em programa")
    public ResponseEntity<Boolean> usuarioTemRole(
            @RequestParam Long usuarioId,
            @RequestParam Long programaId,
            @RequestParam String nomeRole
    ) {
        return ResponseEntity.ok(vinculacaoService.usuarioTemRole(usuarioId, programaId, nomeRole));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Cria nova vinculação")
    public ResponseEntity<UsuarioProgramaRoleResponseDTO> create(@Valid @RequestBody UsuarioProgramaRoleCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vinculacaoService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualiza vinculação existente")
    public ResponseEntity<UsuarioProgramaRoleResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioProgramaRoleUpdateDTO dto
    ) {
        return ResponseEntity.ok(vinculacaoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deleta vinculação")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vinculacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/suspender")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Suspende vinculação")
    public ResponseEntity<UsuarioProgramaRoleResponseDTO> suspender(@PathVariable Long id) {
        return ResponseEntity.ok(vinculacaoService.suspender(id));
    }

    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Reativa vinculação")
    public ResponseEntity<UsuarioProgramaRoleResponseDTO> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(vinculacaoService.reativar(id));
    }

    @PatchMapping("/{id}/desligar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Desliga usuário do programa")
    public ResponseEntity<UsuarioProgramaRoleResponseDTO> desligar(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDesligamento
    ) {
        return ResponseEntity.ok(vinculacaoService.desligar(id, dataDesligamento));
    }

    @GetMapping("/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Retorna estatísticas gerais de vinculações")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        return ResponseEntity.ok(vinculacaoService.getEstatisticas());
    }

    @GetMapping("/estatisticas/programa/{programaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Retorna estatísticas de um programa específico")
    public ResponseEntity<Map<String, Object>> getEstatisticasPorPrograma(@PathVariable Long programaId) {
        return ResponseEntity.ok(vinculacaoService.getEstatisticasPorPrograma(programaId));
    }
}
