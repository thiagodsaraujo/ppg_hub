package com.ppghub.domain.service;

import com.ppghub.application.dto.enums.ResultadoBancaDTO;
import com.ppghub.application.dto.request.BancaCreateRequest;
import com.ppghub.application.dto.request.BancaUpdateRequest;
import com.ppghub.application.dto.response.BancaResponse;
import com.ppghub.application.mapper.BancaMapper;
import com.ppghub.config.CacheConfig;
import com.ppghub.domain.exception.BusinessRuleException;
import com.ppghub.domain.exception.EntityNotFoundException;
import com.ppghub.domain.model.ComposicaoBanca;
import com.ppghub.domain.service.banca.validator.BancaValidatorFactory;
import com.ppghub.infrastructure.persistence.entity.*;
import com.ppghub.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final BancaValidatorFactory validatorFactory;

    // Regras de negócio foram movidas para os validadores específicos:
    // - DefesaComposicaoValidator: regras de bancas de defesa
    // - QualificacaoComposicaoValidator: regras de bancas de qualificação

    public List<BancaResponse> findAll() {
        log.debug("Buscando todas as bancas");
        return mapper.toResponseList(repository.findAll());
    }

    @Cacheable(value = CacheConfig.BANCAS_CACHE, key = "#id", unless = "#result == null")
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
    @CacheEvict(value = CacheConfig.BANCAS_CACHE, allEntries = true)
    public BancaResponse create(BancaCreateRequest request) {
        log.info("Criando nova banca para discente ID: {}", request.getDiscenteId());

        // Validar que o discente existe
        DiscenteEntity discente = discenteRepository.findById(request.getDiscenteId())
                .orElseThrow(() -> new EntityNotFoundException("Discente", request.getDiscenteId()));

        // Validar que o programa existe
        ProgramaEntity programa = programaRepository.findById(request.getProgramaId())
                .orElseThrow(() -> new EntityNotFoundException("Programa", request.getProgramaId()));

        // Validar conflito de horário
        validarConflitoHorario(request.getDiscenteId(), request.getDataHora());

        // Criar a banca
        BancaEntity entity = mapper.toEntity(request);
        entity.setDiscente(discente);
        entity.setPrograma(programa);

        // ✅ Validar composição da banca se houver membros
        if (entity.getMembros() != null && !entity.getMembros().isEmpty()) {
            validarComposicaoBanca(entity);
        }

        BancaEntity saved = repository.save(entity);
        log.info("Banca criada com sucesso: ID {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = CacheConfig.BANCAS_CACHE, key = "#id")
    public Optional<BancaResponse> update(Long id, BancaUpdateRequest request) {
        log.info("Atualizando banca ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    // Validar se a banca pode ser atualizada
                    if (entity.isRealizada()) {
                        throw new BusinessRuleException("Não é possível atualizar uma banca já realizada");
                    }

                    // Se alterou data/hora, validar conflito
                    if (request.getDataHora() != null && !request.getDataHora().equals(entity.getDataHora())) {
                        validarConflitoHorario(entity.getDiscente().getId(), request.getDataHora());
                    }

                    mapper.updateEntityFromRequest(request, entity);

                    // ✅ Validar composição da banca se houver membros
                    if (entity.getMembros() != null && !entity.getMembros().isEmpty()) {
                        validarComposicaoBanca(entity);
                    }

                    BancaEntity updated = repository.save(entity);
                    log.info("Banca atualizada com sucesso: ID {}", updated.getId());
                    return mapper.toResponse(updated);
                });
    }

    /**
     * Marca a banca como realizada e registra o resultado.
     */
    @Transactional
    @CacheEvict(value = CacheConfig.BANCAS_CACHE, key = "#id")
    public Optional<BancaResponse> marcarComoRealizada(Long id, ResultadoBancaDTO resultadoDTO) {
        log.info("Marcando banca como realizada: ID {}, Resultado: {}", id, resultadoDTO);

        // Converter DTO para entidade enum
        BancaEntity.ResultadoBanca resultado = convertResultadoDTO(resultadoDTO);

        return repository.findById(id)
                .map(entity -> {
                    // Validar que a banca ainda não foi realizada
                    if (entity.isRealizada()) {
                        throw new BusinessRuleException("Banca já foi realizada");
                    }

                    // Validar composição antes de marcar como realizada
                    if (entity.getMembros() != null && !entity.getMembros().isEmpty()) {
                        validarComposicaoBanca(entity);
                    }

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
    @CacheEvict(value = CacheConfig.BANCAS_CACHE, key = "#id")
    public Optional<BancaResponse> cancelar(Long id, String motivo) {
        log.info("Cancelando banca ID: {}", id);

        return repository.findById(id)
                .map(entity -> {
                    if (!entity.podeCancelar()) {
                        throw new BusinessRuleException("Banca não pode ser cancelada no status atual: " + entity.getStatusBanca());
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
    @CacheEvict(value = CacheConfig.BANCAS_CACHE, key = "#id")
    public Optional<BancaResponse> reagendar(Long id, LocalDateTime novaDataHora) {
        log.info("Reagendando banca ID: {} para {}", id, novaDataHora);

        return repository.findById(id)
                .map(entity -> {
                    if (!entity.podeReagendar()) {
                        throw new BusinessRuleException("Banca não pode ser reagendada no status atual: " + entity.getStatusBanca());
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
    @CacheEvict(value = CacheConfig.BANCAS_CACHE, key = "#id")
    public boolean delete(Long id) {
        log.info("Deletando banca ID: {}", id);

        Optional<BancaEntity> banca = repository.findById(id);
        if (banca.isEmpty()) {
            return false;
        }

        // Não permitir deletar bancas realizadas
        if (banca.get().isRealizada()) {
            throw new BusinessRuleException("Não é possível deletar uma banca já realizada");
        }

        repository.deleteById(id);
        log.info("Banca deletada com sucesso: ID {}", id);
        return true;
    }

    /**
     * Valida a composição da banca usando o validador apropriado
     * baseado no tipo de banca (Defesa ou Qualificação).
     *
     * <p>
     * Este método aplica o Strategy Pattern para selecionar e executar
     * o validador correto para cada tipo de banca, garantindo que as
     * regras específicas sejam aplicadas.
     * </p>
     *
     * @param banca Entidade da banca a validar
     * @throws BusinessRuleException se a composição violar as regras do tipo de banca
     */
    private void validarComposicaoBanca(BancaEntity banca) {
        log.debug("Validando composição da banca tipo: {}", banca.getTipoBanca());

        ComposicaoBanca composicao = ComposicaoBanca.builder()
                .membros(banca.getMembros())
                .build();

        validatorFactory.getValidator(banca.getTipoBanca())
                .validarComposicao(composicao);

        log.debug("Composição da banca validada com sucesso");
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
            throw new BusinessRuleException(
                "Já existe uma banca agendada para este discente no horário informado (±2 horas)"
            );
        }
    }

    /**
     * Converte ResultadoBancaDTO para BancaEntity.ResultadoBanca.
     * Mantém a separação de camadas ao não expor a entidade de domínio na API.
     */
    private BancaEntity.ResultadoBanca convertResultadoDTO(ResultadoBancaDTO dto) {
        return switch (dto) {
            case APROVADO -> BancaEntity.ResultadoBanca.APROVADO;
            case APROVADO_COM_RESTRICOES -> BancaEntity.ResultadoBanca.APROVADO_COM_RESTRICOES;
            case APROVADO_COM_CORRECOES -> BancaEntity.ResultadoBanca.APROVADO_COM_CORRECOES;
            case REPROVADO -> BancaEntity.ResultadoBanca.REPROVADO;
        };
    }
}
