package com.ppghub.domain.service;

import com.ppghub.application.dto.request.DiscenteCreateRequest;
import com.ppghub.application.dto.request.DiscenteUpdateRequest;
import com.ppghub.application.dto.response.DiscenteResponse;
import com.ppghub.application.mapper.DiscenteMapper;
import com.ppghub.infrastructure.persistence.entity.DiscenteEntity;
import com.ppghub.infrastructure.persistence.entity.DocenteEntity;
import com.ppghub.infrastructure.persistence.entity.ProgramaEntity;
import com.ppghub.infrastructure.persistence.repository.JpaDiscenteRepository;
import com.ppghub.infrastructure.persistence.repository.JpaDocenteRepository;
import com.ppghub.infrastructure.persistence.repository.JpaProgramaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service de domínio para Discente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiscenteService {

    private final JpaDiscenteRepository repository;
    private final JpaProgramaRepository programaRepository;
    private final JpaDocenteRepository docenteRepository;
    private final DiscenteMapper mapper;

    public List<DiscenteResponse> findAll() {
        log.debug("Buscando todos os discentes");
        return mapper.toResponseList(repository.findAll());
    }

    public Optional<DiscenteResponse> findById(Long id) {
        log.debug("Buscando discente por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public Optional<DiscenteResponse> findByMatricula(String matricula) {
        log.debug("Buscando discente por matrícula: {}", matricula);
        return repository.findByMatricula(matricula)
                .map(mapper::toResponse);
    }

    public Optional<DiscenteResponse> findByEmail(String email) {
        log.debug("Buscando discente por email: {}", email);
        return repository.findByEmail(email)
                .map(mapper::toResponse);
    }

    public List<DiscenteResponse> findByPrograma(Long programaId) {
        log.debug("Buscando discentes do programa ID: {}", programaId);
        return mapper.toResponseList(repository.findByProgramaId(programaId));
    }

    public Page<DiscenteResponse> findByPrograma(Long programaId, Pageable pageable) {
        log.debug("Buscando discentes do programa ID: {} com paginação", programaId);
        return repository.findByProgramaId(programaId, pageable)
                .map(mapper::toResponse);
    }

    public List<DiscenteResponse> findByOrientador(Long orientadorId) {
        log.debug("Buscando discentes do orientador ID: {}", orientadorId);
        return mapper.toResponseList(repository.findByOrientadorId(orientadorId));
    }

    public List<DiscenteResponse> findDiscentesAtivos(Long programaId) {
        log.debug("Buscando discentes ativos do programa ID: {}", programaId);
        return mapper.toResponseList(repository.findDiscentesAtivosByPrograma(programaId));
    }

    public List<DiscenteResponse> findDiscentesCandidatosDefesa() {
        log.debug("Buscando discentes candidatos a defesa");
        return mapper.toResponseList(repository.findDiscentesCandidatosDefesa());
    }

    public Page<DiscenteResponse> searchByNome(String nome, Pageable pageable) {
        log.debug("Buscando discentes por nome: {}", nome);
        return repository.searchByNome(nome, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public DiscenteResponse create(DiscenteCreateRequest request) {
        log.info("Criando novo discente: {}", request.getNome());

        // Validar que o programa existe
        ProgramaEntity programa = programaRepository.findById(request.getProgramaId())
                .orElseThrow(() -> new IllegalArgumentException("Programa não encontrado: " + request.getProgramaId()));

        // Validar matrícula única
        if (repository.findByMatricula(request.getMatricula()).isPresent()) {
            throw new IllegalArgumentException("Já existe um discente com esta matrícula: " + request.getMatricula());
        }

        // Validar email único
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um discente com este email: " + request.getEmail());
        }

        // Validar CPF único (se informado)
        if (request.getCpf() != null && repository.findByCpf(request.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um discente com este CPF");
        }

        // Validar ORCID único (se informado)
        if (request.getOrcid() != null && repository.findByOrcid(request.getOrcid()).isPresent()) {
            throw new IllegalArgumentException("Já existe um discente com este ORCID");
        }

        // Validar Lattes único (se informado)
        if (request.getLattesId() != null && repository.findByLattesId(request.getLattesId()).isPresent()) {
            throw new IllegalArgumentException("Já existe um discente com este Lattes ID");
        }

        DiscenteEntity entity = mapper.toEntity(request);
        entity.setPrograma(programa);

        // Adicionar orientador se informado
        if (request.getOrientadorId() != null) {
            DocenteEntity orientador = docenteRepository.findById(request.getOrientadorId())
                    .orElseThrow(() -> new IllegalArgumentException("Orientador não encontrado: " + request.getOrientadorId()));
            entity.setOrientador(orientador);
        }

        DiscenteEntity saved = repository.save(entity);
        log.info("Discente criado com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Transactional
    public Optional<DiscenteResponse> update(Long id, DiscenteUpdateRequest request) {
        log.info("Atualizando discente ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    // Validar email único (se alterado)
                    if (request.getEmail() != null && !request.getEmail().equals(entity.getEmail())) {
                        repository.findByEmail(request.getEmail()).ifPresent(existing -> {
                            throw new IllegalArgumentException("Já existe um discente com este email");
                        });
                    }

                    // Validar CPF único (se alterado)
                    if (request.getCpf() != null && !request.getCpf().equals(entity.getCpf())) {
                        repository.findByCpf(request.getCpf()).ifPresent(existing -> {
                            throw new IllegalArgumentException("Já existe um discente com este CPF");
                        });
                    }

                    mapper.updateEntityFromRequest(request, entity);

                    // Atualizar orientador se informado
                    if (request.getOrientadorId() != null) {
                        DocenteEntity orientador = docenteRepository.findById(request.getOrientadorId())
                                .orElseThrow(() -> new IllegalArgumentException("Orientador não encontrado"));
                        entity.setOrientador(orientador);
                    }

                    DiscenteEntity updated = repository.save(entity);
                    log.info("Discente atualizado com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        log.info("Deletando discente ID: {}", id);

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        log.info("Discente deletado com sucesso: ID {}", id);
        return true;
    }
}
