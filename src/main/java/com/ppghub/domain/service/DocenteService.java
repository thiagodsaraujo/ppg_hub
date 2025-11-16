package com.ppghub.domain.service;

import com.ppghub.application.dto.request.DocenteCreateRequest;
import com.ppghub.application.dto.response.DocenteResponse;
import com.ppghub.application.mapper.DocenteMapper;
import com.ppghub.infrastructure.persistence.entity.DocenteEntity;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaDocenteRepository;
import com.ppghub.infrastructure.persistence.repository.JpaInstituicaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ppghub.config.CacheConfig.DOCENTES_CACHE;

/**
 * Service de domínio para Docente.
 * Encapsula a lógica de negócio relacionada a docentes/pesquisadores.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DocenteService {

    private final JpaDocenteRepository repository;
    private final JpaInstituicaoRepository instituicaoRepository;
    private final DocenteMapper mapper;

    public List<DocenteResponse> findAll() {
        log.debug("Buscando todos os docentes");
        return mapper.toResponseList(repository.findAll());
    }

    @Cacheable(value = DOCENTES_CACHE, key = "#id")
    public Optional<DocenteResponse> findById(Long id) {
        log.debug("Buscando docente por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public Optional<DocenteResponse> findByCpf(String cpf) {
        log.debug("Buscando docente por CPF");
        return repository.findByCpf(cpf)
                .map(mapper::toResponse);
    }

    public Optional<DocenteResponse> findByLattesId(String lattesId) {
        log.debug("Buscando docente por Lattes ID: {}", lattesId);
        return repository.findByLattesId(lattesId)
                .map(mapper::toResponse);
    }

    public Optional<DocenteResponse> findByOrcid(String orcid) {
        log.debug("Buscando docente por ORCID: {}", orcid);
        return repository.findByOrcid(orcid)
                .map(mapper::toResponse);
    }

    public Optional<DocenteResponse> findByOpenalexAuthorId(String openalexAuthorId) {
        log.debug("Buscando docente por OpenAlex Author ID: {}", openalexAuthorId);
        return repository.findByOpenalexAuthorId(openalexAuthorId)
                .map(mapper::toResponse);
    }

    public List<DocenteResponse> findByInstituicao(Long instituicaoId) {
        log.debug("Buscando docentes da instituição ID: {}", instituicaoId);
        return mapper.toResponseList(repository.findByInstituicaoId(instituicaoId));
    }

    public List<DocenteResponse> findAtivosByInstituicao(Long instituicaoId) {
        log.debug("Buscando docentes ativos da instituição ID: {}", instituicaoId);
        return mapper.toResponseList(repository.findDocentesAtivosByInstituicao(instituicaoId));
    }

    public List<DocenteResponse> findDocentesNeedingSync(int daysThreshold) {
        log.debug("Buscando docentes que precisam de sincronização (threshold: {} dias)", daysThreshold);
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(daysThreshold);
        return mapper.toResponseList(repository.findDocentesNeedingSync(thresholdDate));
    }

    public List<DocenteResponse> findDocentesSemOpenAlexId() {
        log.debug("Buscando docentes sem OpenAlex ID");
        return mapper.toResponseList(repository.findDocentesSemOpenAlexId());
    }

    @Transactional
    @CacheEvict(value = DOCENTES_CACHE, allEntries = true)
    public DocenteResponse create(DocenteCreateRequest request) {
        log.info("Criando novo docente: {}", request.getNomeCompleto());

        // Validar que a instituição existe
        InstituicaoEntity instituicao = instituicaoRepository.findById(request.getInstituicaoId())
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada: " + request.getInstituicaoId()));

        // Validar CPF único
        if (request.getCpf() != null && repository.findByCpf(request.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um docente com este CPF");
        }

        // Validar Lattes único
        if (request.getLattesId() != null && repository.findByLattesId(request.getLattesId()).isPresent()) {
            throw new IllegalArgumentException("Já existe um docente com este Lattes ID");
        }

        // Validar ORCID único
        if (request.getOrcid() != null && repository.findByOrcid(request.getOrcid()).isPresent()) {
            throw new IllegalArgumentException("Já existe um docente com este ORCID");
        }

        DocenteEntity entity = mapper.toEntity(request);
        entity.setInstituicao(instituicao);

        DocenteEntity saved = repository.save(entity);
        log.info("Docente criado com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = DOCENTES_CACHE, key = "#id")
    public Optional<DocenteResponse> update(Long id, DocenteCreateRequest request) {
        log.info("Atualizando docente ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    mapper.updateEntityFromRequest(request, entity);
                    DocenteEntity updated = repository.save(entity);
                    log.info("Docente atualizado com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    @CacheEvict(value = DOCENTES_CACHE, key = "#id")
    public boolean delete(Long id) {
        log.info("Deletando docente ID: {}", id);

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        log.info("Docente deletado com sucesso: ID {}", id);
        return true;
    }

    @Transactional
    @CacheEvict(value = DOCENTES_CACHE, key = "#id")
    public Optional<DocenteResponse> desativar(Long id) {
        log.info("Desativando docente ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    entity.setAtivo(false);
                    DocenteEntity updated = repository.save(entity);
                    log.info("Docente desativado com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }
}
