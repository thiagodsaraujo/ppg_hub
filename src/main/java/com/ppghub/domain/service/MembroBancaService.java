package com.ppghub.domain.service;

import com.ppghub.application.dto.request.MembroBancaCreateRequest;
import com.ppghub.application.dto.response.MembroBancaResponse;
import com.ppghub.application.mapper.MembroBancaMapper;
import com.ppghub.infrastructure.persistence.entity.*;
import com.ppghub.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service de domínio para MembroBanca.
 * Gerencia a composição das bancas e o ciclo de vida dos convites.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MembroBancaService {

    private final JpaMembroBancaRepository repository;
    private final JpaBancaRepository bancaRepository;
    private final JpaDocenteRepository docenteRepository;
    private final JpaProfessorExternoRepository professorExternoRepository;
    private final MembroBancaMapper mapper;

    public List<MembroBancaResponse> findAll() {
        log.debug("Buscando todos os membros de banca");
        return mapper.toResponseList(repository.findAll());
    }

    public Optional<MembroBancaResponse> findById(Long id) {
        log.debug("Buscando membro de banca por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public List<MembroBancaResponse> findByBanca(Long bancaId) {
        log.debug("Buscando membros da banca ID: {}", bancaId);
        return mapper.toResponseList(repository.findByBancaIdOrderByOrdem(bancaId));
    }

    public List<MembroBancaResponse> findTitularesByBanca(Long bancaId) {
        log.debug("Buscando membros titulares da banca ID: {}", bancaId);
        return mapper.toResponseList(repository.findTitularesByBanca(bancaId));
    }

    public List<MembroBancaResponse> findSuplentesByBanca(Long bancaId) {
        log.debug("Buscando membros suplentes da banca ID: {}", bancaId);
        return mapper.toResponseList(repository.findSuplentesByBanca(bancaId));
    }

    public List<MembroBancaResponse> findByDocente(Long docenteId) {
        log.debug("Buscando participações do docente ID: {}", docenteId);
        return mapper.toResponseList(repository.findByDocenteId(docenteId));
    }

    public List<MembroBancaResponse> findByProfessorExterno(Long professorExternoId) {
        log.debug("Buscando participações do professor externo ID: {}", professorExternoId);
        return mapper.toResponseList(repository.findByProfessorExternoId(professorExternoId));
    }

    @Transactional
    public MembroBancaResponse addMembro(Long bancaId, MembroBancaCreateRequest request) {
        log.info("Adicionando membro à banca ID: {}", bancaId);

        // Validar que a banca existe
        BancaEntity banca = bancaRepository.findById(bancaId)
                .orElseThrow(() -> new IllegalArgumentException("Banca não encontrada: " + bancaId));

        // Validar que a banca não foi realizada
        if (banca.isRealizada()) {
            throw new IllegalStateException("Não é possível adicionar membros a uma banca já realizada");
        }

        // Validar que exatamente um entre docente ou professorExterno foi informado
        boolean temDocente = request.getDocenteId() != null;
        boolean temProfessorExterno = request.getProfessorExternoId() != null;

        if (!temDocente && !temProfessorExterno) {
            throw new IllegalArgumentException("Deve ser informado docenteId OU professorExternoId");
        }

        if (temDocente && temProfessorExterno) {
            throw new IllegalArgumentException("Não pode informar docenteId E professorExternoId ao mesmo tempo");
        }

        MembroBancaEntity entity = mapper.toEntity(request);
        entity.setBanca(banca);

        // Adicionar docente ou professor externo
        if (temDocente) {
            DocenteEntity docente = docenteRepository.findById(request.getDocenteId())
                    .orElseThrow(() -> new IllegalArgumentException("Docente não encontrado: " + request.getDocenteId()));

            // Validar se docente já está na banca
            if (repository.existsByBancaAndDocente(bancaId, request.getDocenteId())) {
                throw new IllegalArgumentException("Docente já está cadastrado nesta banca");
            }

            entity.setDocente(docente);
        } else {
            ProfessorExternoEntity professorExterno = professorExternoRepository.findById(request.getProfessorExternoId())
                    .orElseThrow(() -> new IllegalArgumentException("Professor externo não encontrado: " + request.getProfessorExternoId()));

            // Validar se professor externo já está na banca
            if (repository.existsByBancaAndProfessorExterno(bancaId, request.getProfessorExternoId())) {
                throw new IllegalArgumentException("Professor externo já está cadastrado nesta banca");
            }

            entity.setProfessorExterno(professorExterno);
        }

        MembroBancaEntity saved = repository.save(entity);
        log.info("Membro adicionado à banca com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    /**
     * Confirma a participação de um membro na banca.
     */
    @Transactional
    public Optional<MembroBancaResponse> confirmarParticipacao(Long id) {
        log.info("Confirmando participação do membro ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    if (entity.isConfirmado()) {
                        throw new IllegalStateException("Participação já foi confirmada");
                    }

                    entity.confirmarParticipacao();
                    MembroBancaEntity updated = repository.save(entity);
                    log.info("Participação confirmada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    /**
     * Recusa a participação de um membro na banca.
     */
    @Transactional
    public Optional<MembroBancaResponse> recusarParticipacao(Long id, String motivo) {
        log.info("Recusando participação do membro ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    if (entity.isRecusado()) {
                        throw new IllegalStateException("Participação já foi recusada");
                    }

                    entity.recusarParticipacao(motivo);
                    MembroBancaEntity updated = repository.save(entity);
                    log.info("Participação recusada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    /**
     * Envia convite para o membro.
     */
    @Transactional
    public Optional<MembroBancaResponse> enviarConvite(Long id) {
        log.info("Enviando convite para membro ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    if (entity.getStatusConvite() != MembroBancaEntity.StatusConvite.PENDENTE) {
                        throw new IllegalStateException("Convite já foi enviado");
                    }

                    entity.setStatusConvite(MembroBancaEntity.StatusConvite.ENVIADO);
                    entity.setDataConvite(java.time.LocalDateTime.now());

                    MembroBancaEntity updated = repository.save(entity);
                    log.info("Convite enviado com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        log.info("Deletando membro de banca ID: {}", id);

        Optional<MembroBancaEntity> membro = repository.findById(id);
        if (membro.isEmpty()) {
            return false;
        }

        // Validar que a banca não foi realizada
        if (membro.get().getBanca().isRealizada()) {
            throw new IllegalStateException("Não é possível remover membros de uma banca já realizada");
        }

        repository.deleteById(id);
        log.info("Membro de banca deletado com sucesso: ID {}", id);
        return true;
    }

    /**
     * Remove um membro da banca (por bancaId e membroId).
     */
    @Transactional
    public boolean removeMembro(Long bancaId, Long membroId) {
        log.info("Removendo membro ID: {} da banca ID: {}", membroId, bancaId);

        Optional<MembroBancaEntity> membro = repository.findById(membroId);
        if (membro.isEmpty()) {
            return false;
        }

        // Validar que o membro pertence à banca informada
        if (!membro.get().getBanca().getId().equals(bancaId)) {
            throw new IllegalArgumentException("Membro não pertence a esta banca");
        }

        return delete(membroId);
    }
}
