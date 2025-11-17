package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.ProfessorExternoCreateRequest;
import com.ppghub.application.dto.request.ProfessorExternoUpdateRequest;
import com.ppghub.application.dto.response.PagedResponse;
import com.ppghub.application.dto.response.ProfessorExternoResponse;
import com.ppghub.domain.service.ProfessorExternoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Professores Externos.
 */
@RestController
@RequestMapping("/v1/professores-externos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Professores Externos", description = "Endpoints para gerenciamento de professores externos (não cadastrados como docentes)")
public class ProfessorExternoController {

    private final ProfessorExternoService professorExternoService;

    @GetMapping
    @Operation(summary = "Listar todos os professores externos", description = "Retorna lista de todos os professores externos")
    public ResponseEntity<List<ProfessorExternoResponse>> listarTodos() {
        log.info("Listando todos os professores externos");
        return ResponseEntity.ok(professorExternoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar professor externo por ID", description = "Retorna um professor externo específico pelo ID")
    public ResponseEntity<ProfessorExternoResponse> buscarPorId(
            @Parameter(description = "ID do professor externo") @PathVariable Long id) {
        log.info("Buscando professor externo por ID: {}", id);
        return professorExternoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar professor externo por email", description = "Retorna professor externo pelo email")
    public ResponseEntity<ProfessorExternoResponse> buscarPorEmail(
            @Parameter(description = "Email do professor externo") @PathVariable String email) {
        log.info("Buscando professor externo por email: {}", email);
        return professorExternoService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orcid/{orcid}")
    @Operation(summary = "Buscar professor externo por ORCID", description = "Retorna professor externo pelo ORCID")
    public ResponseEntity<ProfessorExternoResponse> buscarPorOrcid(
            @Parameter(description = "ORCID") @PathVariable String orcid) {
        log.info("Buscando professor externo por ORCID: {}", orcid);
        return professorExternoService.findByOrcid(orcid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar professores externos ativos", description = "Retorna professores externos ativos")
    public ResponseEntity<List<ProfessorExternoResponse>> listarAtivos() {
        log.info("Listando professores externos ativos");
        return ResponseEntity.ok(professorExternoService.findProfessoresAtivos());
    }

    @GetMapping("/nao-validados")
    @Operation(summary = "Listar professores externos não validados", description = "Retorna professores externos que ainda não foram validados")
    public ResponseEntity<List<ProfessorExternoResponse>> listarNaoValidados() {
        log.info("Listando professores externos não validados");
        return ResponseEntity.ok(professorExternoService.findProfessoresNaoValidados());
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar professores externos por nome", description = "Busca professores externos por nome (parcial)")
    public ResponseEntity<PagedResponse<ProfessorExternoResponse>> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome") @RequestParam String nome,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Buscando professores externos por nome: {}", nome);
        return ResponseEntity.ok(PagedResponse.of(professorExternoService.searchByNome(nome, pageable)));
    }

    @PostMapping
    @Operation(summary = "Criar novo professor externo", description = "Cria um novo professor externo no sistema")
    public ResponseEntity<ProfessorExternoResponse> criar(
            @Valid @RequestBody ProfessorExternoCreateRequest request) {
        log.info("Criando novo professor externo: {}", request.getNome());
        ProfessorExternoResponse created = professorExternoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/find-or-create")
    @Operation(summary = "Buscar ou criar professor externo",
               description = "Busca professor externo por identificadores (email, ORCID, Lattes). Se não encontrar, cria novo registro.")
    public ResponseEntity<ProfessorExternoResponse> findOrCreate(
            @Valid @RequestBody ProfessorExternoCreateRequest request) {
        log.info("Buscando ou criando professor externo: {}", request.getEmail());
        ProfessorExternoResponse response = professorExternoService.findOrCreate(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar professor externo", description = "Atualiza os dados de um professor externo existente")
    public ResponseEntity<ProfessorExternoResponse> atualizar(
            @Parameter(description = "ID do professor externo") @PathVariable Long id,
            @Valid @RequestBody ProfessorExternoUpdateRequest request) {
        log.info("Atualizando professor externo ID: {}", id);
        return professorExternoService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/validar")
    @Operation(summary = "Marcar professor como validado", description = "Marca os dados do professor externo como validados")
    public ResponseEntity<ProfessorExternoResponse> marcarComoValidado(
            @Parameter(description = "ID do professor externo") @PathVariable Long id) {
        log.info("Marcando professor externo como validado: ID {}", id);
        return professorExternoService.marcarComoValidado(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar professor externo", description = "Marca um professor externo como inativo")
    public ResponseEntity<ProfessorExternoResponse> inativar(
            @Parameter(description = "ID do professor externo") @PathVariable Long id) {
        log.info("Inativando professor externo ID: {}", id);
        return professorExternoService.inativar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar professor externo", description = "Remove um professor externo do sistema")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do professor externo") @PathVariable Long id) {
        log.info("Deletando professor externo ID: {}", id);
        return professorExternoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
