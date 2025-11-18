package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.metrica_docente.MetricaDocenteCreateDTO;
import br.edu.ppg.hub.academic.application.dto.metrica_docente.MetricaDocenteResponseDTO;
import br.edu.ppg.hub.academic.application.service.MetricaDocenteService;
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
import java.util.Optional;

/**
 * Controller REST para gerenciamento de Métricas de Docentes
 *
 * @author PPG Hub
 * @since 1.0
 */
@RestController
@RequestMapping("/api/metricas-docentes")
@RequiredArgsConstructor
@Tag(name = "Métricas de Docentes", description = "Endpoints para gerenciamento de métricas acadêmicas de docentes")
@SecurityRequirement(name = "bearer-jwt")
public class MetricaDocenteController {

    private final MetricaDocenteService metricaDocenteService;

    /**
     * Registra uma nova métrica para um docente
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Registrar nova métrica", description = "Registra métricas acadêmicas de um docente")
    public ResponseEntity<MetricaDocenteResponseDTO> registrar(@Valid @RequestBody MetricaDocenteCreateDTO dto) {
        MetricaDocenteResponseDTO response = metricaDocenteService.registrarNovaMetrica(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca métrica por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Buscar métrica por ID", description = "Retorna uma métrica específica")
    public ResponseEntity<MetricaDocenteResponseDTO> buscarPorId(@PathVariable Long id) {
        MetricaDocenteResponseDTO response = metricaDocenteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca todas as métricas de um docente
     */
    @GetMapping("/docente/{docenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar métricas de um docente", description = "Retorna histórico de métricas de um docente")
    public ResponseEntity<Page<MetricaDocenteResponseDTO>> buscarPorDocente(
            @PathVariable Long docenteId,
            @PageableDefault(size = 20, sort = "dataColeta,desc") Pageable pageable
    ) {
        Page<MetricaDocenteResponseDTO> response = metricaDocenteService.buscarPorDocente(docenteId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca histórico completo de métricas de um docente
     */
    @GetMapping("/docente/{docenteId}/historico")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Histórico de métricas", description = "Retorna todo o histórico de métricas ordenado por data")
    public ResponseEntity<List<MetricaDocenteResponseDTO>> getHistorico(@PathVariable Long docenteId) {
        List<MetricaDocenteResponseDTO> response = metricaDocenteService.getHistoricoMetricas(docenteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca a métrica mais recente de um docente
     */
    @GetMapping("/docente/{docenteId}/ultima")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Última métrica", description = "Retorna a métrica mais recente de um docente")
    public ResponseEntity<MetricaDocenteResponseDTO> getUltima(@PathVariable Long docenteId) {
        Optional<MetricaDocenteResponseDTO> response = metricaDocenteService.getUltimaMetrica(docenteId);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    /**
     * Busca métricas de um docente por fonte
     */
    @GetMapping("/docente/{docenteId}/fonte/{fonte}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Métricas por fonte", description = "Filtra métricas de um docente por fonte de dados")
    public ResponseEntity<List<MetricaDocenteResponseDTO>> buscarPorFonte(
            @PathVariable Long docenteId,
            @PathVariable String fonte
    ) {
        List<MetricaDocenteResponseDTO> response = metricaDocenteService.buscarPorDocenteEFonte(docenteId, fonte);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca docentes com alta produtividade de um programa
     */
    @GetMapping("/programa/{programaId}/alta-produtividade")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Docentes com alta produtividade", description = "Retorna docentes com H-index >= 10")
    public ResponseEntity<List<MetricaDocenteResponseDTO>> buscarAltaProdutividade(@PathVariable Long programaId) {
        List<MetricaDocenteResponseDTO> response = metricaDocenteService.buscarDocentesAltaProdutividade(programaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca docentes que atendem aos critérios mínimos da CAPES
     */
    @GetMapping("/programa/{programaId}/atendem-capes")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Docentes que atendem CAPES", description = "Retorna docentes com >= 3 publicações nos últimos 5 anos")
    public ResponseEntity<List<MetricaDocenteResponseDTO>> buscarQueAtendemCapes(@PathVariable Long programaId) {
        List<MetricaDocenteResponseDTO> response = metricaDocenteService.buscarDocentesQueAtendemCapes(programaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deleta uma métrica
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar métrica", description = "Remove uma métrica do sistema")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        metricaDocenteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
