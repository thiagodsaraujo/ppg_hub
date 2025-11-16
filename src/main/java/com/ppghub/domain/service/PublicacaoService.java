package com.ppghub.domain.service;

import com.ppghub.application.dto.request.PublicacaoCreateRequest;
import com.ppghub.application.dto.response.PublicacaoResponse;
import com.ppghub.application.mapper.PublicacaoMapper;
import com.ppghub.infrastructure.persistence.entity.PublicacaoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaPublicacaoRepository;
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

import static com.ppghub.config.CacheConfig.PUBLICACOES_CACHE;

/**
 * Service de domínio para Publicação.
 * Encapsula a lógica de negócio relacionada a publicações científicas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicacaoService {

    private final JpaPublicacaoRepository repository;
    private final PublicacaoMapper mapper;

    public List<PublicacaoResponse> findAll() {
        log.debug("Buscando todas as publicações");
        return mapper.toResponseList(repository.findAll());
    }

    @Cacheable(value = PUBLICACOES_CACHE, key = "#id")
    public Optional<PublicacaoResponse> findById(Long id) {
        log.debug("Buscando publicação por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public Optional<PublicacaoResponse> findByOpenalexWorkId(String openalexWorkId) {
        log.debug("Buscando publicação por OpenAlex Work ID: {}", openalexWorkId);
        return repository.findByOpenalexWorkId(openalexWorkId)
                .map(mapper::toResponse);
    }

    public Optional<PublicacaoResponse> findByDoi(String doi) {
        log.debug("Buscando publicação por DOI: {}", doi);
        return repository.findByDoi(doi)
                .map(mapper::toResponse);
    }

    public List<PublicacaoResponse> findByAno(Integer ano) {
        log.debug("Buscando publicações do ano: {}", ano);
        return mapper.toResponseList(repository.findByAnoPublicacao(ano));
    }

    public List<PublicacaoResponse> findByTipo(String tipo) {
        log.debug("Buscando publicações por tipo: {}", tipo);
        return mapper.toResponseList(repository.findByTipo(tipo));
    }

    public Page<PublicacaoResponse> findByInstituicao(Long instituicaoId, Pageable pageable) {
        log.debug("Buscando publicações da instituição ID: {}", instituicaoId);
        return repository.findByInstituicaoId(instituicaoId, pageable)
                .map(mapper::toResponse);
    }

    public List<PublicacaoResponse> findByDocente(Long docenteId) {
        log.debug("Buscando publicações do docente ID: {}", docenteId);
        return mapper.toResponseList(repository.findByDocenteId(docenteId));
    }

    public Page<PublicacaoResponse> findMaisCitadas(Pageable pageable) {
        log.debug("Buscando publicações mais citadas");
        return repository.findByOrderByCitedByCountDesc(pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = PUBLICACOES_CACHE, allEntries = true)
    public PublicacaoResponse create(PublicacaoCreateRequest request) {
        log.info("Criando nova publicação: {}", request.getTitulo());

        // Validar que não existe publicação com mesmo OpenAlex ID
        if (repository.existsByOpenalexWorkId(request.getOpenalexWorkId())) {
            throw new IllegalArgumentException("Já existe uma publicação com este OpenAlex Work ID");
        }

        // Validar DOI único
        if (request.getDoi() != null && repository.existsByDoi(request.getDoi())) {
            throw new IllegalArgumentException("Já existe uma publicação com este DOI");
        }

        PublicacaoEntity entity = mapper.toEntity(request);
        PublicacaoEntity saved = repository.save(entity);
        log.info("Publicação criada com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = PUBLICACOES_CACHE, key = "#id")
    public Optional<PublicacaoResponse> update(Long id, PublicacaoCreateRequest request) {
        log.info("Atualizando publicação ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    mapper.updateEntityFromRequest(request, entity);
                    PublicacaoEntity updated = repository.save(entity);
                    log.info("Publicação atualizada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    @CacheEvict(value = PUBLICACOES_CACHE, key = "#id")
    public boolean delete(Long id) {
        log.info("Deletando publicação ID: {}", id);

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        log.info("Publicação deletada com sucesso: ID {}", id);
        return true;
    }

    public boolean existsByOpenalexWorkId(String openalexWorkId) {
        return repository.existsByOpenalexWorkId(openalexWorkId);
    }

    public boolean existsByDoi(String doi) {
        return repository.existsByDoi(doi);
    }
}
