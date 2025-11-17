package com.ppghub.domain.service;

import com.ppghub.application.dto.request.ProgramaCreateRequest;
import com.ppghub.application.dto.response.ProgramaResponse;
import com.ppghub.application.mapper.ProgramaMapper;
import com.ppghub.domain.exception.BusinessRuleException;
import com.ppghub.infrastructure.persistence.entity.InstituicaoEntity;
import com.ppghub.infrastructure.persistence.entity.ProgramaEntity;
import com.ppghub.infrastructure.persistence.repository.JpaBancaRepository;
import com.ppghub.infrastructure.persistence.repository.JpaDiscenteRepository;
import com.ppghub.infrastructure.persistence.repository.JpaInstituicaoRepository;
import com.ppghub.infrastructure.persistence.repository.JpaProgramaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ppghub.config.CacheConfig.PROGRAMAS_CACHE;

/**
 * Service de domínio para Programa.
 * Encapsula a lógica de negócio relacionada a programas de pós-graduação.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProgramaService {

    private final JpaProgramaRepository repository;
    private final JpaInstituicaoRepository instituicaoRepository;
    private final JpaDiscenteRepository discenteRepository;
    private final JpaBancaRepository bancaRepository;
    private final ProgramaMapper mapper;

    public List<ProgramaResponse> findAll() {
        log.debug("Buscando todos os programas");
        return mapper.toResponseList(repository.findAll());
    }

    @Cacheable(value = PROGRAMAS_CACHE, key = "#id")
    public Optional<ProgramaResponse> findById(Long id) {
        log.debug("Buscando programa por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public Optional<ProgramaResponse> findByCodigoCapes(String codigoCapes) {
        log.debug("Buscando programa por código CAPES: {}", codigoCapes);
        return repository.findByCodigoCapes(codigoCapes)
                .map(mapper::toResponse);
    }

    public List<ProgramaResponse> findByInstituicao(Long instituicaoId) {
        log.debug("Buscando programas da instituição ID: {}", instituicaoId);
        return mapper.toResponseList(repository.findByInstituicaoId(instituicaoId));
    }

    public List<ProgramaResponse> findByAreaConhecimento(String areaConhecimento) {
        log.debug("Buscando programas por área de conhecimento: {}", areaConhecimento);
        return mapper.toResponseList(repository.findByAreaConhecimento(areaConhecimento));
    }

    public List<ProgramaResponse> findByStatus(String status) {
        log.debug("Buscando programas por status: {}", status);
        return mapper.toResponseList(repository.findByStatus(status));
    }

    public List<ProgramaResponse> findProgramasAtivosByInstituicao(Long instituicaoId) {
        log.debug("Buscando programas ativos da instituição ID: {}", instituicaoId);
        return mapper.toResponseList(repository.findProgramasAtivosByInstituicao(instituicaoId));
    }

    @Transactional
    @CacheEvict(value = PROGRAMAS_CACHE, allEntries = true)
    public ProgramaResponse create(ProgramaCreateRequest request) {
        log.info("Criando novo programa: {}", request.getNome());

        // Validar que a instituição existe
        InstituicaoEntity instituicao = instituicaoRepository.findById(request.getInstituicaoId())
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada: " + request.getInstituicaoId()));

        // Validar código CAPES único
        if (request.getCodigoCapes() != null && repository.findByCodigoCapes(request.getCodigoCapes()).isPresent()) {
            throw new IllegalArgumentException("Já existe um programa com este código CAPES");
        }

        ProgramaEntity entity = mapper.toEntity(request);
        entity.setInstituicao(instituicao);

        ProgramaEntity saved = repository.save(entity);
        log.info("Programa criado com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = PROGRAMAS_CACHE, key = "#id")
    public Optional<ProgramaResponse> update(Long id, ProgramaCreateRequest request) {
        log.info("Atualizando programa ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    // Validar código CAPES único (excluindo o próprio programa)
                    if (request.getCodigoCapes() != null) {
                        repository.findByCodigoCapes(request.getCodigoCapes()).ifPresent(existing -> {
                            if (!existing.getId().equals(id)) {
                                throw new IllegalArgumentException("Já existe um programa com este código CAPES");
                            }
                        });
                    }

                    mapper.updateEntityFromRequest(request, entity);
                    ProgramaEntity updated = repository.save(entity);
                    log.info("Programa atualizado com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    @CacheEvict(value = PROGRAMAS_CACHE, key = "#id")
    public boolean delete(Long id) {
        log.info("Deletando programa ID: {}", id);

        if (!repository.existsById(id)) {
            return false;
        }

        // Verificar se o programa possui discentes
        long discentesCount = discenteRepository.countByProgramaId(id);
        if (discentesCount > 0) {
            throw new BusinessRuleException(
                String.format("Não é possível deletar o programa. Existem %d discente(s) associado(s). " +
                    "Considere inativar o programa ao invés de deletá-lo.", discentesCount)
            );
        }

        // Verificar se o programa possui bancas
        long bancasCount = bancaRepository.countByProgramaId(id);
        if (bancasCount > 0) {
            throw new BusinessRuleException(
                String.format("Não é possível deletar o programa. Existem %d banca(s) associada(s). " +
                    "Considere inativar o programa ao invés de deletá-lo.", bancasCount)
            );
        }

        repository.deleteById(id);
        log.info("Programa deletado com sucesso: ID {}", id);
        return true;
    }

    @Transactional
    @CacheEvict(value = PROGRAMAS_CACHE, key = "#id")
    public Optional<ProgramaResponse> inativar(Long id) {
        log.info("Inativando programa ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    entity.setStatus("INATIVO");
                    ProgramaEntity updated = repository.save(entity);
                    log.info("Programa inativado com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }
}
