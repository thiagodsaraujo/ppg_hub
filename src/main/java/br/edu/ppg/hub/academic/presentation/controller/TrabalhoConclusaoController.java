package br.edu.ppg.hub.academic.presentation.controller;

import br.edu.ppg.hub.academic.application.dto.trabalho_conclusao.TrabalhoConclusaoCreateDTO;
import br.edu.ppg.hub.academic.application.dto.trabalho_conclusao.TrabalhoConclusaoResponseDTO;
import br.edu.ppg.hub.academic.application.dto.trabalho_conclusao.TrabalhoConclusaoUpdateDTO;
import br.edu.ppg.hub.academic.application.service.TrabalhoConclusaoService;
import br.edu.ppg.hub.academic.domain.enums.StatusTrabalho;
import br.edu.ppg.hub.academic.domain.enums.TipoTrabalho;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para gerenciamento de Trabalhos de Conclusão.
 * Fornece endpoints para CRUD e operações específicas de dissertações e teses.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/trabalhos-conclusao")
@RequiredArgsConstructor
@Tag(name = "Trabalhos de Conclusão", description = "Endpoints para gerenciamento de dissertações e teses")
@SecurityRequirement(name = "bearer-jwt")
public class TrabalhoConclusaoController {

    private final TrabalhoConclusaoService trabalhoConclusaoService;

    /**
     * Cria um novo trabalho de conclusão.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Criar trabalho de conclusão", description = "Registra uma nova dissertação ou tese")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> criar(@Valid @RequestBody TrabalhoConclusaoCreateDTO dto) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca trabalho por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar trabalho por ID", description = "Retorna dados completos de um trabalho de conclusão")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> buscarPorId(@PathVariable Long id) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os trabalhos com paginação.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar todos os trabalhos", description = "Retorna lista paginada de trabalhos de conclusão")
    public ResponseEntity<Page<TrabalhoConclusaoResponseDTO>> listarTodos(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<TrabalhoConclusaoResponseDTO> response = trabalhoConclusaoService.listarTodos(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca trabalho por discente.
     */
    @GetMapping("/discente/{discenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Buscar trabalho por discente", description = "Retorna o trabalho de conclusão de um discente")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> buscarPorDiscente(@PathVariable Long discenteId) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.buscarPorDiscente(discenteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca trabalhos por orientador.
     */
    @GetMapping("/orientador/{orientadorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar trabalhos por orientador", description = "Retorna trabalhos orientados por um docente")
    public ResponseEntity<Page<TrabalhoConclusaoResponseDTO>> buscarPorOrientador(
            @PathVariable Long orientadorId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<TrabalhoConclusaoResponseDTO> response = trabalhoConclusaoService.buscarPorOrientador(orientadorId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca trabalhos por coorientador.
     */
    @GetMapping("/coorientador/{coorientadorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar trabalhos por coorientador", description = "Retorna trabalhos coorientados por um docente")
    public ResponseEntity<Page<TrabalhoConclusaoResponseDTO>> buscarPorCoorientador(
            @PathVariable Long coorientadorId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<TrabalhoConclusaoResponseDTO> response = trabalhoConclusaoService.buscarPorCoorientador(coorientadorId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca trabalhos pendentes de defesa.
     */
    @GetMapping("/pendentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar trabalhos pendentes", description = "Retorna trabalhos qualificados pendentes de defesa")
    public ResponseEntity<List<TrabalhoConclusaoResponseDTO>> buscarPendentes() {
        List<TrabalhoConclusaoResponseDTO> response = trabalhoConclusaoService.buscarTrabalhosPendentes();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca trabalhos por tipo.
     */
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Listar trabalhos por tipo", description = "Retorna trabalhos de um tipo específico")
    public ResponseEntity<Page<TrabalhoConclusaoResponseDTO>> buscarPorTipo(
            @PathVariable TipoTrabalho tipo,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<TrabalhoConclusaoResponseDTO> response = trabalhoConclusaoService.buscarPorTipo(tipo, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca trabalhos por status.
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar trabalhos por status", description = "Retorna trabalhos com status específico")
    public ResponseEntity<Page<TrabalhoConclusaoResponseDTO>> buscarPorStatus(
            @PathVariable StatusTrabalho status,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<TrabalhoConclusaoResponseDTO> response = trabalhoConclusaoService.buscarPorStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca trabalhos por período de defesa.
     */
    @GetMapping("/periodo-defesa")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Listar trabalhos por período", description = "Retorna trabalhos defendidos em um período")
    public ResponseEntity<Page<TrabalhoConclusaoResponseDTO>> buscarPorPeriodoDefesa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 20, sort = "dataDefesa") Pageable pageable
    ) {
        Page<TrabalhoConclusaoResponseDTO> response = trabalhoConclusaoService.buscarPorPeriodoDefesa(dataInicio, dataFim, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza um trabalho de conclusão.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE')")
    @Operation(summary = "Atualizar trabalho", description = "Atualiza dados de um trabalho de conclusão")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TrabalhoConclusaoUpdateDTO dto
    ) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Submete um trabalho para avaliação.
     */
    @PostMapping("/{id}/submeter")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Submeter trabalho", description = "Submete trabalho para avaliação")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> submeter(@PathVariable Long id) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.submeter(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Aprova um trabalho de conclusão.
     */
    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Aprovar trabalho", description = "Aprova um trabalho após defesa")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> aprovar(@PathVariable Long id) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.aprovar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Reprova um trabalho de conclusão.
     */
    @PostMapping("/{id}/reprovar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Reprovar trabalho", description = "Reprova um trabalho após defesa")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> reprovar(@PathVariable Long id) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.reprovar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Publica um trabalho no repositório institucional.
     */
    @PostMapping("/{id}/publicar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Publicar trabalho", description = "Publica trabalho no repositório institucional")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> publicar(@PathVariable Long id) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.publicar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Faz upload do arquivo PDF do trabalho.
     */
    @PostMapping("/{id}/upload-arquivo")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Upload de arquivo", description = "Envia arquivo PDF do trabalho")
    public ResponseEntity<TrabalhoConclusaoResponseDTO> uploadArquivo(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        TrabalhoConclusaoResponseDTO response = trabalhoConclusaoService.uploadArquivo(id, arquivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Faz download do arquivo PDF do trabalho.
     */
    @GetMapping("/{id}/download-arquivo")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'DOCENTE', 'DISCENTE')")
    @Operation(summary = "Download de arquivo", description = "Baixa arquivo PDF do trabalho")
    public ResponseEntity<byte[]> downloadArquivo(@PathVariable Long id) {
        byte[] conteudo = trabalhoConclusaoService.downloadArquivo(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "trabalho-" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(conteudo);
    }

    /**
     * Deleta um trabalho de conclusão.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @Operation(summary = "Deletar trabalho", description = "Remove um trabalho de conclusão")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        trabalhoConclusaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
