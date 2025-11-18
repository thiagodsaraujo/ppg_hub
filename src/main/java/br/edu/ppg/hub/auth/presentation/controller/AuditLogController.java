package br.edu.ppg.hub.auth.presentation.controller;

import br.edu.ppg.hub.auth.application.dto.audit.AuditLogResponseDTO;
import br.edu.ppg.hub.auth.application.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller para consulta de Logs de Auditoria.
 * Somente leitura - logs são criados automaticamente pelo AuditAspect.
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Logs de Auditoria", description = "Endpoints para consulta de logs de auditoria (somente leitura)")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista todos os logs de auditoria com paginação")
    public ResponseEntity<Page<AuditLogResponseDTO>> findAll(
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Busca log por ID")
    public ResponseEntity<AuditLogResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.findById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista logs de um usuário específico")
    public ResponseEntity<Page<AuditLogResponseDTO>> findByUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditService.findByUsuario(usuarioId, pageable));
    }

    @GetMapping("/acao/{acao}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista logs de uma ação específica (CREATE, UPDATE, DELETE, LOGIN, LOGOUT)")
    public ResponseEntity<Page<AuditLogResponseDTO>> findByAcao(
            @PathVariable String acao,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditService.findByAcao(acao, pageable));
    }

    @GetMapping("/entidade/{entidade}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista logs de uma entidade específica (Usuario, Programa, etc)")
    public ResponseEntity<Page<AuditLogResponseDTO>> findByEntidade(
            @PathVariable String entidade,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditService.findByEntidade(entidade, pageable));
    }

    @GetMapping("/entidade/{entidade}/{entidadeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista logs de uma entidade e ID específicos")
    public ResponseEntity<Page<AuditLogResponseDTO>> findByEntidadeAndId(
            @PathVariable String entidade,
            @PathVariable Long entidadeId,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditService.findByEntidadeAndId(entidade, entidadeId, pageable));
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista logs por período")
    public ResponseEntity<Page<AuditLogResponseDTO>> findByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditService.findByPeriodo(inicio, fim, pageable));
    }

    @GetMapping("/usuario/{usuarioId}/recentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Lista logs recentes de um usuário")
    public ResponseEntity<Page<AuditLogResponseDTO>> findRecentByUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditService.findRecentByUsuario(usuarioId, pageable));
    }

    @GetMapping("/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'SECRETARIA')")
    @Operation(summary = "Retorna estatísticas de auditoria")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        return ResponseEntity.ok(auditService.getEstatisticas());
    }

    @DeleteMapping("/limpar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Limpa logs antigos (manutenção)")
    public ResponseEntity<Void> limparLogsAntigos(@RequestParam(defaultValue = "365") int diasRetencao) {
        auditService.limparLogsAntigos(diasRetencao);
        return ResponseEntity.noContent().build();
    }
}
