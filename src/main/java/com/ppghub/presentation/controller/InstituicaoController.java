package com.ppghub.presentation.controller;

import com.ppghub.application.dto.request.InstituicaoCreateRequest;
import com.ppghub.application.dto.response.InstituicaoResponse;
import com.ppghub.application.mapper.InstituicaoMapper;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaInstituicaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ppghub.config.CacheConfig.INSTITUICOES_CACHE;

/**
 * Controller REST para gerenciamento de Instituições.
 */
@RestController
@RequestMapping("/v1/instituicoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Instituições", description = "Endpoints para gerenciamento de instituições de ensino superior")
public class InstituicaoController {

    private final JpaInstituicaoRepository instituicaoRepository;
    private final InstituicaoMapper instituicaoMapper;

    @GetMapping
    @Operation(summary = "Listar todas as instituições", description = "Retorna lista paginada de todas as instituições")
    public ResponseEntity<List<InstituicaoResponse>> listarTodas() {
        log.info("Listando todas as instituições");
        List<InstituicaoEntity> instituicoes = instituicaoRepository.findAll();
        return ResponseEntity.ok(instituicaoMapper.toResponseList(instituicoes));
    }

    @GetMapping("/{id}")
    @Cacheable(value = INSTITUICOES_CACHE, key = "#id")
    @Operation(summary = "Buscar instituição por ID", description = "Retorna uma instituição específica pelo ID")
    public ResponseEntity<InstituicaoResponse> buscarPorId(
            @Parameter(description = "ID da instituição") @PathVariable Long id) {
        log.info("Buscando instituição por ID: {}", id);
        return instituicaoRepository.findById(id)
                .map(instituicaoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sigla/{sigla}")
    @Operation(summary = "Buscar instituição por sigla", description = "Retorna instituição pela sigla")
    public ResponseEntity<InstituicaoResponse> buscarPorSigla(
            @Parameter(description = "Sigla da instituição") @PathVariable String sigla) {
        log.info("Buscando instituição por sigla: {}", sigla);
        return instituicaoRepository.findBySigla(sigla)
                .map(instituicaoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/openalex/{openalexId}")
    @Operation(summary = "Buscar instituição por OpenAlex ID", description = "Retorna instituição pelo ID do OpenAlex")
    public ResponseEntity<InstituicaoResponse> buscarPorOpenAlexId(
            @Parameter(description = "OpenAlex Institution ID") @PathVariable String openalexId) {
        log.info("Buscando instituição por OpenAlex ID: {}", openalexId);
        return instituicaoRepository.findByOpenalexInstitutionId(openalexId)
                .map(instituicaoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ror/{rorId}")
    @Operation(summary = "Buscar instituição por ROR ID", description = "Retorna instituição pelo Research Organization Registry ID")
    public ResponseEntity<InstituicaoResponse> buscarPorRorId(
            @Parameter(description = "ROR ID") @PathVariable String rorId) {
        log.info("Buscando instituição por ROR ID: {}", rorId);
        return instituicaoRepository.findByRorId(rorId)
                .map(instituicaoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar instituições por nome", description = "Busca instituições que contenham o termo no nome")
    public ResponseEntity<Page<InstituicaoResponse>> buscarPorNome(
            @Parameter(description = "Nome da instituição") @RequestParam String nome,
            Pageable pageable) {
        log.info("Buscando instituições por nome: {}", nome);
        Page<InstituicaoEntity> page = instituicaoRepository.searchByNome(nome, pageable);
        return ResponseEntity.ok(page.map(instituicaoMapper::toResponse));
    }

    @GetMapping("/ativas")
    @Operation(summary = "Listar instituições ativas", description = "Retorna apenas instituições ativas")
    public ResponseEntity<List<InstituicaoResponse>> listarAtivas() {
        log.info("Listando instituições ativas");
        List<InstituicaoEntity> instituicoes = instituicaoRepository.findByAtivoTrue();
        return ResponseEntity.ok(instituicaoMapper.toResponseList(instituicoes));
    }

    @PostMapping
    @CacheEvict(value = INSTITUICOES_CACHE, allEntries = true)
    @Operation(summary = "Criar nova instituição", description = "Cria uma nova instituição no sistema")
    public ResponseEntity<InstituicaoResponse> criar(
            @Valid @RequestBody InstituicaoCreateRequest request) {
        log.info("Criando nova instituição: {}", request.getNome());

        // Validações de negócio
        if (request.getCnpj() != null && instituicaoRepository.existsByCnpj(request.getCnpj())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (instituicaoRepository.existsBySigla(request.getSigla())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        InstituicaoEntity entity = instituicaoMapper.toEntity(request);
        InstituicaoEntity saved = instituicaoRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(instituicaoMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    @CacheEvict(value = INSTITUICOES_CACHE, key = "#id")
    @Operation(summary = "Atualizar instituição", description = "Atualiza os dados de uma instituição existente")
    public ResponseEntity<InstituicaoResponse> atualizar(
            @Parameter(description = "ID da instituição") @PathVariable Long id,
            @Valid @RequestBody InstituicaoCreateRequest request) {
        log.info("Atualizando instituição ID: {}", id);

        return instituicaoRepository.findById(id)
                .map(entity -> {
                    instituicaoMapper.updateEntityFromRequest(request, entity);
                    InstituicaoEntity updated = instituicaoRepository.save(entity);
                    return ResponseEntity.ok(instituicaoMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = INSTITUICOES_CACHE, key = "#id")
    @Operation(summary = "Deletar instituição", description = "Remove uma instituição do sistema")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da instituição") @PathVariable Long id) {
        log.info("Deletando instituição ID: {}", id);

        if (!instituicaoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        instituicaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @CacheEvict(value = INSTITUICOES_CACHE, key = "#id")
    @Operation(summary = "Desativar instituição", description = "Marca uma instituição como inativa (soft delete)")
    public ResponseEntity<InstituicaoResponse> desativar(
            @Parameter(description = "ID da instituição") @PathVariable Long id) {
        log.info("Desativando instituição ID: {}", id);

        return instituicaoRepository.findById(id)
                .map(entity -> {
                    entity.setAtivo(false);
                    InstituicaoEntity updated = instituicaoRepository.save(entity);
                    return ResponseEntity.ok(instituicaoMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
