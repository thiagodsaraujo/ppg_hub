package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.oferta_disciplina.OfertaDisciplinaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.oferta_disciplina.OfertaDisciplinaResponseDTO;
import br.edu.ppg.hub.academic.application.dto.oferta_disciplina.OfertaDisciplinaUpdateDTO;
import br.edu.ppg.hub.academic.application.service.OfertaDisciplinaService;
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
 * Controller REST para gerenciamento de Ofertas de Disciplinas
 *
 * @author PPG Hub
 * @since 1.0
 */
@RestController
@RequestMapping("/api/ofertas-disciplinas")
@RequiredArgsConstructor
@Tag(name = "Ofertas de Disciplinas", description = "Endpoints para gerenciamento de ofertas de disciplinas")
@SecurityRequirement(name = "bearer-jwt")
public class OfertaDisciplinaController {

    private final OfertaDisciplinaService ofertaDisciplinaService;

    /**
     * Cria uma nova oferta de disciplina
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Criar nova oferta", description = "Cria uma nova oferta de disciplina para um período")
    public ResponseEntity<OfertaDisciplinaResponseDTO> criar(@Valid @RequestBody OfertaDisciplinaCreateDTO dto) {
        OfertaDisciplinaResponseDTO response = ofertaDisciplinaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza uma oferta existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualizar oferta", description = "Atualiza dados de uma oferta existente")
    public ResponseEntity<OfertaDisciplinaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OfertaDisciplinaUpdateDTO dto
    ) {
        OfertaDisciplinaResponseDTO response = ofertaDisciplinaService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca oferta por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar oferta por ID", description = "Retorna dados completos de uma oferta")
    public ResponseEntity<OfertaDisciplinaResponseDTO> buscarPorId(@PathVariable Long id) {
        OfertaDisciplinaResponseDTO response = ofertaDisciplinaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca todas as ofertas (paginado)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar todas as ofertas", description = "Retorna lista paginada de ofertas")
    public ResponseEntity<Page<OfertaDisciplinaResponseDTO>> buscarTodas(
            @PageableDefault(size = 20, sort = "periodo,desc") Pageable pageable
    ) {
        Page<OfertaDisciplinaResponseDTO> response = ofertaDisciplinaService.buscarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca ofertas por período
     */
    @GetMapping("/periodo/{periodo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar ofertas por período", description = "Retorna ofertas de um período específico (ex: 2024.1)")
    public ResponseEntity<Page<OfertaDisciplinaResponseDTO>> buscarPorPeriodo(
            @PathVariable String periodo,
            @PageableDefault(size = 20, sort = "disciplina.codigo") Pageable pageable
    ) {
        Page<OfertaDisciplinaResponseDTO> response = ofertaDisciplinaService.buscarPorPeriodo(periodo, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca ofertas por docente
     */
    @GetMapping("/docente/{docenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar ofertas por docente", description = "Retorna ofertas de um docente específico")
    public ResponseEntity<Page<OfertaDisciplinaResponseDTO>> buscarPorDocente(
            @PathVariable Long docenteId,
            @PageableDefault(size = 20, sort = "periodo,desc") Pageable pageable
    ) {
        Page<OfertaDisciplinaResponseDTO> response = ofertaDisciplinaService.buscarPorDocente(docenteId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca ofertas com vagas disponíveis
     */
    @GetMapping("/com-vagas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar ofertas com vagas", description = "Retorna ofertas com vagas disponíveis para matrícula")
    public ResponseEntity<List<OfertaDisciplinaResponseDTO>> buscarComVagasDisponiveis() {
        List<OfertaDisciplinaResponseDTO> response = ofertaDisciplinaService.buscarComVagasDisponiveis();
        return ResponseEntity.ok(response);
    }

    /**
     * Abre inscrições para uma oferta
     */
    @PatchMapping("/{id}/abrir-inscricoes")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Abrir inscrições", description = "Abre as inscrições para matrícula na oferta")
    public ResponseEntity<OfertaDisciplinaResponseDTO> abrirInscricoes(@PathVariable Long id) {
        OfertaDisciplinaResponseDTO response = ofertaDisciplinaService.abrirInscricoes(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Fecha inscrições para uma oferta
     */
    @PatchMapping("/{id}/fechar-inscricoes")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Fechar inscrições", description = "Fecha as inscrições para matrícula na oferta")
    public ResponseEntity<OfertaDisciplinaResponseDTO> fecharInscricoes(@PathVariable Long id) {
        OfertaDisciplinaResponseDTO response = ofertaDisciplinaService.fecharInscricoes(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Inicia uma oferta (coloca em andamento)
     */
    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Iniciar oferta", description = "Inicia a oferta (coloca em andamento)")
    public ResponseEntity<OfertaDisciplinaResponseDTO> iniciar(@PathVariable Long id) {
        OfertaDisciplinaResponseDTO response = ofertaDisciplinaService.iniciar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Conclui uma oferta
     */
    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Concluir oferta", description = "Conclui a oferta após lançamento de todas as notas")
    public ResponseEntity<OfertaDisciplinaResponseDTO> concluir(@PathVariable Long id) {
        OfertaDisciplinaResponseDTO response = ofertaDisciplinaService.concluir(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela uma oferta
     */
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Cancelar oferta", description = "Cancela uma oferta antes da conclusão")
    public ResponseEntity<OfertaDisciplinaResponseDTO> cancelar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo
    ) {
        OfertaDisciplinaResponseDTO response = ofertaDisciplinaService.cancelar(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Deleta uma oferta (só se não tiver matrículas)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar oferta", description = "Remove uma oferta do sistema (só se não tiver matrículas)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ofertaDisciplinaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtém vagas disponíveis de uma oferta
     */
    @GetMapping("/{id}/vagas-disponiveis")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Consultar vagas", description = "Retorna o número de vagas disponíveis na oferta")
    public ResponseEntity<Map<String, Integer>> getVagasDisponiveis(@PathVariable Long id) {
        Integer vagas = ofertaDisciplinaService.getVagasDisponiveis(id);
        return ResponseEntity.ok(Map.of("vagasDisponiveis", vagas));
    }

    /**
     * Obtém estatísticas de uma oferta
     */
    @GetMapping("/{id}/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Estatísticas da oferta", description = "Retorna estatísticas completas da oferta")
    public ResponseEntity<Map<String, Object>> getEstatisticas(@PathVariable Long id) {
        Map<String, Object> estatisticas = ofertaDisciplinaService.getEstatisticas(id);
        return ResponseEntity.ok(estatisticas);
    }
}
