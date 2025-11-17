package com.ppghub.domain.service;

import com.ppghub.application.dto.request.ProfessorExternoCreateRequest;
import com.ppghub.application.dto.request.ProfessorExternoUpdateRequest;
import com.ppghub.application.dto.response.ProfessorExternoResponse;
import com.ppghub.application.mapper.ProfessorExternoMapper;
import com.ppghub.domain.exception.DuplicateEntityException;
import com.ppghub.infrastructure.persistence.entity.ProfessorExternoEntity;
import com.ppghub.infrastructure.persistence.repository.JpaProfessorExternoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service de domínio para ProfessorExterno.
 * Implementa o padrão find-or-create para lidar com professores externos
 * que não estão cadastrados no sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfessorExternoService {

    private final JpaProfessorExternoRepository repository;
    private final ProfessorExternoMapper mapper;

    public List<ProfessorExternoResponse> findAll() {
        log.debug("Buscando todos os professores externos");
        return mapper.toResponseList(repository.findAll());
    }

    public Optional<ProfessorExternoResponse> findById(Long id) {
        log.debug("Buscando professor externo por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public Optional<ProfessorExternoResponse> findByEmail(String email) {
        log.debug("Buscando professor externo por email: {}", email);
        return repository.findByEmail(email)
                .map(mapper::toResponse);
    }

    public Optional<ProfessorExternoResponse> findByOrcid(String orcid) {
        log.debug("Buscando professor externo por ORCID: {}", orcid);
        return repository.findByOrcid(orcid)
                .map(mapper::toResponse);
    }

    public List<ProfessorExternoResponse> findProfessoresAtivos() {
        log.debug("Buscando professores externos ativos");
        return mapper.toResponseList(repository.findProfessoresAtivos());
    }

    public List<ProfessorExternoResponse> findProfessoresNaoValidados() {
        log.debug("Buscando professores externos não validados");
        return mapper.toResponseList(repository.findProfessoresNaoValidados());
    }

    public Page<ProfessorExternoResponse> searchByNome(String nome, Pageable pageable) {
        log.debug("Buscando professores externos por nome: {}", nome);
        return repository.searchByNome(nome, pageable)
                .map(mapper::toResponse);
    }

    /**
     * Implementação do padrão find-or-create.
     * Busca um professor externo por identificadores únicos (email, ORCID, Lattes, OpenAlex).
     * Se não encontrar, cria um novo registro.
     *
     * Este método é essencial para o cenário de professores externos não cadastrados
     * durante a criação de bancas.
     */
    @Transactional
    public ProfessorExternoResponse findOrCreate(ProfessorExternoCreateRequest request) {
        log.info("Buscando ou criando professor externo: {}", request.getEmail());

        // Tentar encontrar por identificadores
        Optional<ProfessorExternoEntity> existing = repository.findByEmailOrIdentificadores(
            request.getEmail(),
            request.getOrcid(),
            request.getLattesId(),
            request.getOpenalexAuthorId()
        );

        if (existing.isPresent()) {
            log.info("Professor externo já existe: ID {}", existing.get().getId());
            return mapper.toResponse(existing.get());
        }

        // Se não encontrou, criar novo
        log.info("Professor externo não encontrado, criando novo");
        return create(request);
    }

    @Transactional
    public ProfessorExternoResponse create(ProfessorExternoCreateRequest request) {
        log.info("Criando novo professor externo: {}", request.getNome());

        // Validar email único
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEntityException("Professor Externo", "email", request.getEmail());
        }

        // Validar ORCID único (se informado)
        if (request.getOrcid() != null && repository.findByOrcid(request.getOrcid()).isPresent()) {
            throw new DuplicateEntityException("Professor Externo", "ORCID", request.getOrcid());
        }

        // Validar Lattes único (se informado)
        if (request.getLattesId() != null && repository.findByLattesId(request.getLattesId()).isPresent()) {
            throw new DuplicateEntityException("Professor Externo", "Lattes ID", request.getLattesId());
        }

        ProfessorExternoEntity entity = mapper.toEntity(request);
        ProfessorExternoEntity saved = repository.save(entity);
        log.info("Professor externo criado com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Transactional
    public Optional<ProfessorExternoResponse> update(Long id, ProfessorExternoUpdateRequest request) {
        log.info("Atualizando professor externo ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    // Validar email único (se alterado)
                    if (request.getEmail() != null && !request.getEmail().equals(entity.getEmail())) {
                        repository.findByEmail(request.getEmail()).ifPresent(existing -> {
                            throw new DuplicateEntityException("Professor Externo", "email", request.getEmail());
                        });
                    }

                    // Validar ORCID único (se alterado)
                    if (request.getOrcid() != null && !request.getOrcid().equals(entity.getOrcid())) {
                        repository.findByOrcid(request.getOrcid()).ifPresent(existing -> {
                            throw new DuplicateEntityException("Professor Externo", "ORCID", request.getOrcid());
                        });
                    }

                    mapper.updateEntityFromRequest(request, entity);

                    ProfessorExternoEntity updated = repository.save(entity);
                    log.info("Professor externo atualizado com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    /**
     * Marca um professor externo como validado.
     * Indica que os dados foram verificados/enriquecidos via fontes externas.
     */
    @Transactional
    public Optional<ProfessorExternoResponse> marcarComoValidado(Long id) {
        log.info("Marcando professor externo como validado: ID {}", id);

        return repository.findById(id)
                .map(entity -> {
                    entity.marcarComoValidado();
                    ProfessorExternoEntity updated = repository.save(entity);
                    log.info("Professor externo marcado como validado: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        log.info("Deletando professor externo ID: {}", id);

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        log.info("Professor externo deletado com sucesso: ID {}", id);
        return true;
    }

    /**
     * Inativa um professor externo (soft delete).
     * Preferível ao delete quando há referências em bancas.
     */
    @Transactional
    public Optional<ProfessorExternoResponse> inativar(Long id) {
        log.info("Inativando professor externo ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    entity.setAtivo(false);
                    ProfessorExternoEntity updated = repository.save(entity);
                    log.info("Professor externo inativado com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }
}
