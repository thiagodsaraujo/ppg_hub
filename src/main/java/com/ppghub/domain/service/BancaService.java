package com.ppghub.domain.service;

import com.ppghub.application.dto.request.BancaCreateRequest;
import com.ppghub.application.dto.request.BancaUpdateRequest;
import com.ppghub.application.dto.response.BancaResponse;
import com.ppghub.application.mapper.BancaMapper;
import com.ppghub.infrastructure.persistence.entity.*;
import com.ppghub.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service de domínio para Banca.
 * Implementa regras de negócio complexas para composição e validação de bancas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BancaService {

    private final JpaBancaRepository repository;
    private final JpaDiscenteRepository discenteRepository;
    private final JpaProgramaRepository programaRepository;
    private final JpaMembroBancaRepository membroBancaRepository;
    private final BancaMapper mapper;

    // Regras de negócio
    private static final int MIN_MEMBROS_TITULARES = 3;
    private static final int MAX_MEMBROS_TITULARES = 5;
    private static final int MIN_MEMBROS_EXTERNOS = 1;

    public List<BancaResponse> findAll() {
        log.debug("Buscando todas as bancas");
        return mapper.toResponseList(repository.findAll());
    }

    public Optional<BancaResponse> findById(Long id) {
        log.debug("Buscando banca por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public List<BancaResponse> findByDiscente(Long discenteId) {
        log.debug("Buscando bancas do discente ID: {}", discenteId);
        return mapper.toResponseList(repository.findByDiscenteIdOrderByDataHora(discenteId));
    }

    public Page<BancaResponse> findByPrograma(Long programaId, Pageable pageable) {
        log.debug("Buscando bancas do programa ID: {} com paginação", programaId);
        return repository.findByProgramaId(programaId, pageable)
                .map(mapper::toResponse);
    }

    public List<BancaResponse> findProximasBancas() {
        log.debug("Buscando próximas bancas");
        return mapper.toResponseList(repository.findProximasBancas(LocalDateTime.now()));
    }

    public Page<BancaResponse> findProximasBancas(Pageable pageable) {
        log.debug("Buscando próximas bancas com paginação");
        return repository.findProximasBancasPage(LocalDateTime.now(), pageable)
                .map(mapper::toResponse);
    }

    public List<BancaResponse> findBancasSemAta() {
        log.debug("Buscando bancas sem ata");
        return mapper.toResponseList(repository.findBancasSemAta());
    }

    @Transactional
    public BancaResponse create(BancaCreateRequest request) {
        log.info("Criando nova banca para discente ID: {}", request.getDiscenteId());

        // Validar que o discente existe
        DiscenteEntity discente = discenteRepository.findById(request.getDiscenteId())
                .orElseThrow(() -> new IllegalArgumentException("Discente não encontrado: " + request.getDiscenteId()));

        // Validar que o programa existe
        ProgramaEntity programa = programaRepository.findById(request.getProgramaId())
                .orElseThrow(() -> new IllegalArgumentException("Programa não encontrado: " + request.getProgramaId()));

        // Validar conflito de horário
        validarConflitoHorario(request.getDiscenteId(), request.getDataHora());

        // Criar a banca
        BancaEntity entity = mapper.toEntity(request);
        entity.setDiscente(discente);
        entity.setPrograma(programa);

        BancaEntity saved = repository.save(entity);
        log.info("Banca criada com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Transactional
    public Optional<BancaResponse> update(Long id, BancaUpdateRequest request) {
        log.info("Atualizando banca ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    // Validar se a banca pode ser atualizada
                    if (entity.isRealizada()) {
                        throw new IllegalStateException("Não é possível atualizar uma banca já realizada");
                    }

                    // Se alterou data/hora, validar conflito
                    if (request.getDataHora() != null && !request.getDataHora().equals(entity.getDataHora())) {
                        validarConflitoHorario(entity.getDiscente().getId(), request.getDataHora());
                    }

                    mapper.updateEntityFromRequest(request, entity);

                    BancaEntity updated = repository.save(entity);
                    log.info("Banca atualizada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    /**
     * Marca a banca como realizada e registra o resultado.
     */
    @Transactional
    public Optional<BancaResponse> marcarComoRealizada(Long id, BancaEntity.ResultadoBanca resultado) {
        log.info("Marcando banca como realizada: ID {}, Resultado: {}", id, resultado);

        return repository.findById(id)
                .map(entity -> {
                    // Validar que a banca ainda não foi realizada
                    if (entity.isRealizada()) {
                        throw new IllegalStateException("Banca já foi realizada");
                    }

                    // Validar composição antes de marcar como realizada
                    validarComposicaoBanca(id);

                    entity.marcarComoRealizada(resultado);
                    BancaEntity updated = repository.save(entity);
                    log.info("Banca marcada como realizada: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    /**
     * Cancela uma banca agendada.
     */
    @Transactional
    public Optional<BancaResponse> cancelar(Long id, String motivo) {
        log.info("Cancelando banca ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    if (!entity.podeCancelar()) {
                        throw new IllegalStateException("Banca não pode ser cancelada no status atual: " + entity.getStatusBanca());
                    }

                    entity.setStatusBanca(BancaEntity.StatusBanca.CANCELADA);
                    if (motivo != null && !motivo.isBlank()) {
                        String obs = entity.getObservacoes() != null ? entity.getObservacoes() + "\n" : "";
                        entity.setObservacoes(obs + "Cancelamento: " + motivo);
                    }

                    BancaEntity updated = repository.save(entity);
                    log.info("Banca cancelada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    /**
     * Reagenda uma banca para nova data/hora.
     */
    @Transactional
    public Optional<BancaResponse> reagendar(Long id, LocalDateTime novaDataHora) {
        log.info("Reagendando banca ID: {} para {}", id, novaDataHora);

        return repository.findById(id)
                .map(entity -> {
                    if (!entity.podeReagendar()) {
                        throw new IllegalStateException("Banca não pode ser reagendada no status atual: " + entity.getStatusBanca());
                    }

                    // Validar conflito de horário com a nova data
                    validarConflitoHorario(entity.getDiscente().getId(), novaDataHora);

                    entity.setDataHora(novaDataHora);
                    entity.setStatusBanca(BancaEntity.StatusBanca.REAGENDADA);

                    BancaEntity updated = repository.save(entity);
                    log.info("Banca reagendada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        log.info("Deletando banca ID: {}", id);

        Optional<BancaEntity> banca = repository.findById(id);
        if (banca.isEmpty()) {
            return false;
        }

        // Não permitir deletar bancas realizadas
        if (banca.get().isRealizada()) {
            throw new IllegalStateException("Não é possível deletar uma banca já realizada");
        }

        repository.deleteById(id);
        log.info("Banca deletada com sucesso: ID {}", id);
        return true;
    }

    /**
     * Valida a composição da banca de acordo com as regras de negócio:
     * - Deve ter entre 3 e 5 membros titulares
     * - Deve ter pelo menos 1 membro externo
     * - Não deve ter membros duplicados
     */
    public void validarComposicaoBanca(Long bancaId) {
        log.debug("Validando composição da banca ID: {}", bancaId);

        List<MembroBancaEntity> membros = membroBancaRepository.findByBancaId(bancaId);

        // Contar membros titulares
        long numTitulares = membros.stream()
                .filter(m -> m.getTipoMembro() == MembroBancaEntity.TipoMembro.TITULAR)
                .count();

        if (numTitulares < MIN_MEMBROS_TITULARES) {
            throw new IllegalStateException(
                String.format("Banca deve ter no mínimo %d membros titulares. Atual: %d",
                    MIN_MEMBROS_TITULARES, numTitulares)
            );
        }

        if (numTitulares > MAX_MEMBROS_TITULARES) {
            throw new IllegalStateException(
                String.format("Banca deve ter no máximo %d membros titulares. Atual: %d",
                    MAX_MEMBROS_TITULARES, numTitulares)
            );
        }

        // Contar membros externos
        long numExternos = membros.stream()
                .filter(MembroBancaEntity::isExterno)
                .count();

        if (numExternos < MIN_MEMBROS_EXTERNOS) {
            throw new IllegalStateException(
                String.format("Banca deve ter pelo menos %d membro externo. Atual: %d",
                    MIN_MEMBROS_EXTERNOS, numExternos)
            );
        }

        log.debug("Composição da banca validada: {} titulares, {} externos", numTitulares, numExternos);
    }

    /**
     * Valida se não há conflito de horário para o discente.
     * Considera um range de 4 horas (2 horas antes e 2 horas depois).
     */
    private void validarConflitoHorario(Long discenteId, LocalDateTime dataHora) {
        LocalDateTime inicio = dataHora.minusHours(2);
        LocalDateTime fim = dataHora.plusHours(2);

        List<BancaEntity> conflitos = repository.findBancasComConflitoHorarioDiscente(
            discenteId, inicio, fim
        );

        if (!conflitos.isEmpty()) {
            throw new IllegalStateException(
                "Já existe uma banca agendada para este discente no horário informado (±2 horas)"
            );
        }
    }
}
