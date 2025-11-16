package com.ppghub.domain.service;

import com.ppghub.application.dto.request.InstituicaoCreateRequest;
import com.ppghub.application.dto.response.InstituicaoResponse;
import com.ppghub.application.mapper.InstituicaoMapper;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaInstituicaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ppghub.config.CacheConfig.INSTITUICOES_CACHE;

/**
 * Service de domínio para Instituição.
 * Encapsula a lógica de negócio relacionada a instituições.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InstituicaoService {

    private final JpaInstituicaoRepository repository;
    private final InstituicaoMapper mapper;

    public List<InstituicaoResponse> findAll() {
        log.debug("Buscando todas as instituições");
        return mapper.toResponseList(repository.findAll());
    }

    @Cacheable(value = INSTITUICOES_CACHE, key = "#id")
    public Optional<InstituicaoResponse> findById(Long id) {
        log.debug("Buscando instituição por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public Optional<InstituicaoResponse> findBySigla(String sigla) {
        log.debug("Buscando instituição por sigla: {}", sigla);
        return repository.findBySigla(sigla)
                .map(mapper::toResponse);
    }

    public Optional<InstituicaoResponse> findByOpenalexId(String openalexId) {
        log.debug("Buscando instituição por OpenAlex ID: {}", openalexId);
        return repository.findByOpenalexInstitutionId(openalexId)
                .map(mapper::toResponse);
    }

    public Optional<InstituicaoResponse> findByRorId(String rorId) {
        log.debug("Buscando instituição por ROR ID: {}", rorId);
        return repository.findByRorId(rorId)
                .map(mapper::toResponse);
    }

    public Page<InstituicaoResponse> searchByNome(String nome, Pageable pageable) {
        log.debug("Buscando instituições por nome: {}", nome);
        return repository.searchByNome(nome, pageable)
                .map(mapper::toResponse);
    }

    public List<InstituicaoResponse> findAtivas() {
        log.debug("Buscando instituições ativas");
        return mapper.toResponseList(repository.findByAtivoTrue());
    }

    @Transactional
    @CacheEvict(value = INSTITUICOES_CACHE, allEntries = true)
    public InstituicaoResponse create(InstituicaoCreateRequest request) {
        log.info("Criando nova instituição: {}", request.getNome());

        // Validações de negócio
        if (request.getCnpj() != null && repository.existsByCnpj(request.getCnpj())) {
            throw new IllegalArgumentException("Já existe uma instituição com este CNPJ");
        }
        if (repository.existsBySigla(request.getSigla())) {
            throw new IllegalArgumentException("Já existe uma instituição com esta sigla");
        }

        InstituicaoEntity entity = mapper.toEntity(request);
        InstituicaoEntity saved = repository.save(entity);
        log.info("Instituição criada com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = INSTITUICOES_CACHE, key = "#id")
    public Optional<InstituicaoResponse> update(Long id, InstituicaoCreateRequest request) {
        log.info("Atualizando instituição ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    mapper.updateEntityFromRequest(request, entity);
                    InstituicaoEntity updated = repository.save(entity);
                    log.info("Instituição atualizada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    @CacheEvict(value = INSTITUICOES_CACHE, key = "#id")
    public boolean delete(Long id) {
        log.info("Deletando instituição ID: {}", id);

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        log.info("Instituição deletada com sucesso: ID {}", id);
        return true;
    }

    @Transactional
    @CacheEvict(value = INSTITUICOES_CACHE, key = "#id")
    public Optional<InstituicaoResponse> desativar(Long id) {
        log.info("Desativando instituição ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    entity.setAtivo(false);
                    InstituicaoEntity updated = repository.save(entity);
                    log.info("Instituição desativada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    public boolean existsByCnpj(String cnpj) {
        return repository.existsByCnpj(cnpj);
    }

    public boolean existsBySigla(String sigla) {
        return repository.existsBySigla(sigla);
    }
}
