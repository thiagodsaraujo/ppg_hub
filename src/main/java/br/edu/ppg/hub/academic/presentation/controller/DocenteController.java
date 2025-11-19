package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.docente.DocenteCreateDTO;
import br.edu.ppg.hub.academic.application.dto.docente.DocenteResponseDTO;
import br.edu.ppg.hub.academic.application.dto.docente.DocenteUpdateDTO;
import br.edu.ppg.hub.academic.application.service.DocenteService;
import br.edu.ppg.hub.academic.domain.enums.StatusDocente;
import br.edu.ppg.hub.academic.domain.enums.TipoVinculoDocente;
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
 * Controller REST para gerenciamento de Docentes
 *
 * @author PPG Hub
 * @since 1.0
 */
@RestController
@RequestMapping("/api/docentes")
@RequiredArgsConstructor
@Tag(name = "Docentes", description = "Endpoints para gerenciamento de docentes")
@SecurityRequirement(name = "bearer-jwt")
public class DocenteController {

    private final DocenteService docenteService;

    /**
     * Cria um novo docente
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Criar novo docente", description = "Vincula um docente a um programa")
    public ResponseEntity<DocenteResponseDTO> criar(@Valid @RequestBody DocenteCreateDTO dto) {
        DocenteResponseDTO response = docenteService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza um docente existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualizar docente", description = "Atualiza dados de um docente existente")
    public ResponseEntity<DocenteResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DocenteUpdateDTO dto
    ) {
        DocenteResponseDTO response = docenteService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca docente por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar docente por ID", description = "Retorna dados completos de um docente")
    public ResponseEntity<DocenteResponseDTO> buscarPorId(@PathVariable Long id) {
        DocenteResponseDTO response = docenteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca todos os docentes (paginado)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar todos os docentes", description = "Retorna lista paginada de docentes")
    public ResponseEntity<Page<DocenteResponseDTO>> buscarTodos(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DocenteResponseDTO> response = docenteService.buscarTodos(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca docentes por programa
     */
    @GetMapping("/programa/{programaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar docentes por programa", description = "Retorna docentes de um programa específico")
    public ResponseEntity<Page<DocenteResponseDTO>> buscarPorPrograma(
            @PathVariable Long programaId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DocenteResponseDTO> response = docenteService.buscarPorPrograma(programaId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca docentes por programa e status
     */
    @GetMapping("/programa/{programaId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Listar docentes por programa e status", description = "Filtra docentes por status")
    public ResponseEntity<Page<DocenteResponseDTO>> buscarPorProgramaEStatus(
            @PathVariable Long programaId,
            @PathVariable StatusDocente status,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DocenteResponseDTO> response = docenteService.buscarPorProgramaEStatus(programaId, status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca docentes por tipo de vínculo
     */
    @GetMapping("/tipo-vinculo/{tipoVinculo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Listar docentes por tipo de vínculo", description = "Filtra docentes por tipo de vínculo")
    public ResponseEntity<Page<DocenteResponseDTO>> buscarPorTipoVinculo(
            @PathVariable TipoVinculoDocente tipoVinculo,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DocenteResponseDTO> response = docenteService.buscarPorTipoVinculo(tipoVinculo, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca docentes por linha de pesquisa
     */
    @GetMapping("/linha-pesquisa/{linhaPesquisaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar docentes por linha de pesquisa", description = "Retorna docentes de uma linha de pesquisa")
    public ResponseEntity<Page<DocenteResponseDTO>> buscarPorLinhaPesquisa(
            @PathVariable Long linhaPesquisaId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<DocenteResponseDTO> response = docenteService.buscarPorLinhaPesquisa(linhaPesquisaId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca docentes com orientações disponíveis
     */
    @GetMapping("/programa/{programaId}/orientacoes-disponiveis")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Listar docentes com orientações disponíveis", description = "Retorna docentes que podem orientar novos alunos")
    public ResponseEntity<List<DocenteResponseDTO>> buscarComOrientacoesDisponiveis(
            @PathVariable Long programaId,
            @RequestParam(defaultValue = "8") int limiteOrientacoes
    ) {
        List<DocenteResponseDTO> response = docenteService.buscarComOrientacoesDisponiveis(programaId, limiteOrientacoes);
        return ResponseEntity.ok(response);
    }

    /**
     * Ativa um docente
     */
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Ativar docente", description = "Altera status do docente para Ativo")
    public ResponseEntity<DocenteResponseDTO> ativar(@PathVariable Long id) {
        DocenteResponseDTO response = docenteService.ativar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Desativa um docente
     */
    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Desativar docente", description = "Desliga docente do programa")
    public ResponseEntity<DocenteResponseDTO> desativar(
            @PathVariable Long id,
            @RequestParam String motivo
    ) {
        DocenteResponseDTO response = docenteService.desativar(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Afasta um docente temporariamente
     */
    @PatchMapping("/{id}/afastar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Afastar docente", description = "Afasta docente temporariamente")
    public ResponseEntity<DocenteResponseDTO> afastar(
            @PathVariable Long id,
            @RequestParam String motivo
    ) {
        DocenteResponseDTO response = docenteService.afastar(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Aposenta um docente
     */
    @PatchMapping("/{id}/aposentar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Aposentar docente", description = "Registra aposentadoria do docente")
    public ResponseEntity<DocenteResponseDTO> aposentar(@PathVariable Long id) {
        DocenteResponseDTO response = docenteService.aposentar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Vincula docente a uma linha de pesquisa
     */
    @PatchMapping("/{docenteId}/vincular-linha/{linhaPesquisaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Vincular à linha de pesquisa", description = "Vincula docente a uma linha de pesquisa")
    public ResponseEntity<DocenteResponseDTO> vincularLinhaPesquisa(
            @PathVariable Long docenteId,
            @PathVariable Long linhaPesquisaId
    ) {
        DocenteResponseDTO response = docenteService.vincularLinhaPesquisa(docenteId, linhaPesquisaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Desvincula docente de linha de pesquisa
     */
    @PatchMapping("/{docenteId}/desvincular-linha")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Desvincular de linha de pesquisa", description = "Remove vínculo com linha de pesquisa")
    public ResponseEntity<DocenteResponseDTO> desvincularLinhaPesquisa(@PathVariable Long docenteId) {
        DocenteResponseDTO response = docenteService.desvincularLinhaPesquisa(docenteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna estatísticas de docentes de um programa
     */
    @GetMapping("/programa/{programaId}/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Estatísticas de docentes", description = "Retorna estatísticas agregadas de docentes do programa")
    public ResponseEntity<Map<String, Object>> getEstatisticas(@PathVariable Long programaId) {
        Map<String, Object> stats = docenteService.getEstatisticasPorPrograma(programaId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Deleta um docente
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar docente", description = "Remove docente do sistema (use com cuidado)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        docenteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
