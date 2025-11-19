package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.banca.BancaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.banca.BancaResponseDTO;
import br.edu.ppg.hub.academic.application.dto.banca.BancaUpdateDTO;
import br.edu.ppg.hub.academic.application.dto.membro_banca.MembroBancaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.membro_banca.MembroBancaResponseDTO;
import br.edu.ppg.hub.academic.application.service.BancaService;
import br.edu.ppg.hub.academic.domain.enums.ResultadoBanca;
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
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de Bancas Examinadoras.
 * Fornece endpoints para agendamento, realização e gestão de bancas.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/bancas")
@RequiredArgsConstructor
@Tag(name = "Bancas", description = "Endpoints para gerenciamento de bancas de qualificação e defesa")
@SecurityRequirement(name = "bearer-jwt")
public class BancaController {

    private final BancaService bancaService;

    /**
     * Agenda uma nova banca.
     */
    @PostMapping("/agendar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Agendar banca", description = "Cria e agenda uma nova banca examinadora")
    public ResponseEntity<BancaResponseDTO> agendar(@Valid @RequestBody BancaCreateDTO dto) {
        BancaResponseDTO response = bancaService.agendar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca banca por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar banca por ID", description = "Retorna dados completos de uma banca")
    public ResponseEntity<BancaResponseDTO> buscarPorId(@PathVariable Long id) {
        BancaResponseDTO response = bancaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas as bancas com paginação.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar todas as bancas", description = "Retorna lista paginada de bancas")
    public ResponseEntity<Page<BancaResponseDTO>> listarTodas(
            @PageableDefault(size = 20, sort = "dataAgendada") Pageable pageable
    ) {
        Page<BancaResponseDTO> response = bancaService.listarTodas(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca bancas por discente.
     */
    @GetMapping("/discente/{discenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar bancas por discente", description = "Retorna bancas de um discente")
    public ResponseEntity<Page<BancaResponseDTO>> buscarPorDiscente(
            @PathVariable Long discenteId,
            @PageableDefault(size = 20, sort = "dataAgendada") Pageable pageable
    ) {
        Page<BancaResponseDTO> response = bancaService.buscarPorDiscente(discenteId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca bancas por trabalho de conclusão.
     */
    @GetMapping("/trabalho/{trabalhoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar bancas por trabalho", description = "Retorna bancas relacionadas a um trabalho")
    public ResponseEntity<List<BancaResponseDTO>> buscarPorTrabalho(@PathVariable Long trabalhoId) {
        List<BancaResponseDTO> response = bancaService.buscarPorTrabalho(trabalhoId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca bancas agendadas.
     */
    @GetMapping("/agendadas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar bancas agendadas", description = "Retorna bancas futuras agendadas")
    public ResponseEntity<Page<BancaResponseDTO>> buscarAgendadas(
            @PageableDefault(size = 20, sort = "dataAgendada") Pageable pageable
    ) {
        Page<BancaResponseDTO> response = bancaService.buscarAgendadas(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza uma banca.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Atualizar banca", description = "Atualiza dados de uma banca agendada")
    public ResponseEntity<BancaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody BancaUpdateDTO dto
    ) {
        BancaResponseDTO response = bancaService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Reagenda uma banca para nova data.
     */
    @PutMapping("/{id}/reagendar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Reagendar banca", description = "Altera a data de uma banca agendada")
    public ResponseEntity<BancaResponseDTO> reagendar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate novaData
    ) {
        BancaResponseDTO response = bancaService.reagendar(id, novaData);
        return ResponseEntity.ok(response);
    }

    /**
     * Inicia a realização de uma banca.
     */
    @PostMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Iniciar banca", description = "Marca o início da realização de uma banca")
    public ResponseEntity<BancaResponseDTO> iniciar(@PathVariable Long id) {
        BancaResponseDTO response = bancaService.iniciar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Finaliza uma banca com resultado e ata.
     */
    @PostMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Finalizar banca", description = "Finaliza banca registrando resultado e ata")
    public ResponseEntity<BancaResponseDTO> finalizar(
            @PathVariable Long id,
            @RequestParam ResultadoBanca resultado,
            @RequestBody(required = false) Map<String, Object> ata
    ) {
        BancaResponseDTO response = bancaService.finalizar(id, resultado, ata);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela uma banca agendada.
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Cancelar banca", description = "Cancela uma banca agendada")
    public ResponseEntity<BancaResponseDTO> cancelar(
            @PathVariable Long id,
            @RequestParam String motivo
    ) {
        BancaResponseDTO response = bancaService.cancelar(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona um membro à banca.
     */
    @PostMapping("/{id}/adicionar-membro")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Adicionar membro", description = "Adiciona um membro à composição da banca")
    public ResponseEntity<MembroBancaResponseDTO> adicionarMembro(
            @PathVariable Long id,
            @Valid @RequestBody MembroBancaCreateDTO membroDTO
    ) {
        MembroBancaResponseDTO response = bancaService.adicionarMembro(id, membroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Remove um membro da banca.
     */
    @DeleteMapping("/membro/{membroId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Remover membro", description = "Remove um membro da banca")
    public ResponseEntity<Void> removerMembro(@PathVariable Long membroId) {
        bancaService.removerMembro(membroId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Define o presidente da banca.
     */
    @PostMapping("/{id}/definir-presidente/{membroId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Definir presidente", description = "Define qual membro será o presidente da banca")
    public ResponseEntity<BancaResponseDTO> definirPresidente(
            @PathVariable Long id,
            @PathVariable Long membroId
    ) {
        BancaResponseDTO response = bancaService.definirPresidente(id, membroId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deleta uma banca.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Deletar banca", description = "Remove uma banca do sistema")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        bancaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
