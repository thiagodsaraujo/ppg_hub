package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.matricula_disciplina.MatriculaDisciplinaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.matricula_disciplina.MatriculaDisciplinaResponseDTO;
import br.edu.ppg.hub.academic.application.service.MatriculaDisciplinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de Matrículas em Disciplinas
 *
 * @author PPG Hub
 * @since 1.0
 */
@RestController
@RequestMapping("/api/matriculas-disciplinas")
@RequiredArgsConstructor
@Tag(name = "Matrículas em Disciplinas", description = "Endpoints para gerenciamento de matrículas em disciplinas")
@SecurityRequirement(name = "bearer-jwt")
public class MatriculaDisciplinaController {

    private final MatriculaDisciplinaService matriculaService;

    /**
     * Matricula um discente em uma oferta de disciplina
     */
    @PostMapping("/matricular")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DISCENTE')")
    @Operation(summary = "Matricular discente", description = "Matricula um discente em uma oferta de disciplina (com controle de vagas)")
    public ResponseEntity<MatriculaDisciplinaResponseDTO> matricular(@Valid @RequestBody MatriculaDisciplinaCreateDTO dto) {
        MatriculaDisciplinaResponseDTO response = matriculaService.matricular(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca matrícula por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar matrícula por ID", description = "Retorna dados completos de uma matrícula")
    public ResponseEntity<MatriculaDisciplinaResponseDTO> buscarPorId(@PathVariable Long id) {
        MatriculaDisciplinaResponseDTO response = matriculaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca todas as matrículas de um discente
     */
    @GetMapping("/discente/{discenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar matrículas do discente", description = "Retorna todas as matrículas de um discente")
    public ResponseEntity<Page<MatriculaDisciplinaResponseDTO>> buscarPorDiscente(
            @PathVariable Long discenteId,
            @PageableDefault(size = 20, sort = "dataMatricula,desc") Pageable pageable
    ) {
        Page<MatriculaDisciplinaResponseDTO> response = matriculaService.buscarPorDiscente(discenteId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca todas as matrículas de uma oferta
     */
    @GetMapping("/oferta/{ofertaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar matrículas da oferta", description = "Retorna todas as matrículas de uma oferta")
    public ResponseEntity<Page<MatriculaDisciplinaResponseDTO>> buscarPorOferta(
            @PathVariable Long ofertaId,
            @PageableDefault(size = 50, sort = "discente.usuario.nomeCompleto") Pageable pageable
    ) {
        Page<MatriculaDisciplinaResponseDTO> response = matriculaService.buscarPorOferta(ofertaId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca histórico completo de matrículas de um discente
     */
    @GetMapping("/discente/{discenteId}/historico")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Histórico do discente", description = "Retorna histórico completo de matrículas do discente")
    public ResponseEntity<List<MatriculaDisciplinaResponseDTO>> getHistoricoMatriculas(@PathVariable Long discenteId) {
        List<MatriculaDisciplinaResponseDTO> response = matriculaService.getHistoricoMatriculas(discenteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Tranca uma matrícula
     */
    @PatchMapping("/{id}/trancar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DISCENTE')")
    @Operation(summary = "Trancar matrícula", description = "Tranca uma matrícula ativa (libera a vaga)")
    public ResponseEntity<MatriculaDisciplinaResponseDTO> trancar(@PathVariable Long id) {
        MatriculaDisciplinaResponseDTO response = matriculaService.trancar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lança nota final para uma matrícula
     */
    @PatchMapping("/{id}/lancar-nota")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Lançar nota", description = "Lança a nota final de uma matrícula (0 a 10)")
    public ResponseEntity<MatriculaDisciplinaResponseDTO> lancarNota(
            @PathVariable Long id,
            @RequestParam @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal nota
    ) {
        MatriculaDisciplinaResponseDTO response = matriculaService.lancarNota(id, nota);
        return ResponseEntity.ok(response);
    }

    /**
     * Lança frequência para uma matrícula
     */
    @PatchMapping("/{id}/lancar-frequencia")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Lançar frequência", description = "Lança a frequência percentual de uma matrícula (0 a 100)")
    public ResponseEntity<MatriculaDisciplinaResponseDTO> lancarFrequencia(
            @PathVariable Long id,
            @RequestParam @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal frequencia
    ) {
        MatriculaDisciplinaResponseDTO response = matriculaService.lancarFrequencia(id, frequencia);
        return ResponseEntity.ok(response);
    }

    /**
     * Calcula resultado final (aprovar/reprovar)
     */
    @PatchMapping("/{id}/calcular-resultado")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Calcular resultado", description = "Calcula resultado final da matrícula (aprovado/reprovado)")
    public ResponseEntity<MatriculaDisciplinaResponseDTO> calcularResultado(@PathVariable Long id) {
        MatriculaDisciplinaResponseDTO response = matriculaService.calcularResultado(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Calcula resultados em lote para todas as matrículas de uma oferta
     */
    @PostMapping("/oferta/{ofertaId}/calcular-resultados")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Calcular resultados em lote", description = "Calcula resultados para todas as matrículas de uma oferta")
    public ResponseEntity<Map<String, Object>> calcularResultadosOferta(@PathVariable Long ofertaId) {
        Map<String, Object> resultado = matriculaService.calcularResultadosOferta(ofertaId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtém estatísticas de um discente
     */
    @GetMapping("/discente/{discenteId}/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Estatísticas do discente", description = "Retorna estatísticas completas do discente")
    public ResponseEntity<Map<String, Object>> getEstatisticasDiscente(@PathVariable Long discenteId) {
        Map<String, Object> estatisticas = matriculaService.getEstatisticasDiscente(discenteId);
        return ResponseEntity.ok(estatisticas);
    }

    /**
     * Deleta uma matrícula (só se permitido)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Deletar matrícula", description = "Remove uma matrícula do sistema (só se no status 'Matriculado')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        matriculaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
