package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.banca.BancaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.banca.BancaMapper;
import br.edu.ppg.hub.academic.application.dto.banca.BancaResponseDTO;
import br.edu.ppg.hub.academic.application.dto.banca.BancaUpdateDTO;
import br.edu.ppg.hub.academic.application.dto.membro_banca.MembroBancaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.membro_banca.MembroBancaMapper;
import br.edu.ppg.hub.academic.application.dto.membro_banca.MembroBancaResponseDTO;
import br.edu.ppg.hub.academic.domain.enums.ResultadoBanca;
import br.edu.ppg.hub.academic.domain.enums.TipoBanca;
import br.edu.ppg.hub.academic.domain.model.Banca;
import br.edu.ppg.hub.academic.domain.model.Discente;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.MembroBanca;
import br.edu.ppg.hub.academic.domain.model.TrabalhoConclusao;
import br.edu.ppg.hub.academic.infrastructure.repository.BancaRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.DiscenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.DocenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.MembroBancaRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.TrabalhoConclusaoRepository;
import br.edu.ppg.hub.shared.exception.BusinessException;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para operações com Bancas Examinadoras.
 * Implementa toda a lógica de negócio e validações complexas relacionadas a bancas.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BancaService {

    private final BancaRepository bancaRepository;
    private final DiscenteRepository discenteRepository;
    private final DocenteRepository docenteRepository;
    private final TrabalhoConclusaoRepository trabalhoConclusaoRepository;
    private final MembroBancaRepository membroBancaRepository;
    private final BancaMapper bancaMapper;
    private final MembroBancaMapper membroBancaMapper;

    /**
     * Cria e agenda uma nova banca.
     *
     * @param dto Dados da banca
     * @return Banca criada
     */
    @Transactional
    public BancaResponseDTO agendar(BancaCreateDTO dto) {
        log.info("Agendando nova banca tipo {} para discente: {}", dto.getTipo(), dto.getDiscenteId());

        // Validar data futura
        if (dto.getDataAgendada().isBefore(LocalDate.now())) {
            throw new BusinessException("Data agendada deve ser futura");
        }

        // Buscar entidades relacionadas
        Discente discente = discenteRepository.findById(dto.getDiscenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        // Validar se discente já tem banca do mesmo tipo agendada
        if (bancaRepository.existsByDiscenteIdAndTipoAndStatus(dto.getDiscenteId(), dto.getTipo(), "Agendada")) {
            throw new BusinessException("Discente já possui uma banca deste tipo agendada");
        }

        TrabalhoConclusao trabalho = null;
        if (dto.getTrabalhoConclusaoId() != null) {
            trabalho = trabalhoConclusaoRepository.findById(dto.getTrabalhoConclusaoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trabalho de conclusão não encontrado"));

            // Validar se trabalho pertence ao discente
            if (!trabalho.getDiscente().getId().equals(discente.getId())) {
                throw new BusinessException("Trabalho de conclusão não pertence ao discente informado");
            }

            // Validar se trabalho pode ser defendido
            if (dto.getTipo().isDefesa() && !trabalho.podeDefender()) {
                throw new BusinessException("Trabalho não está pronto para defesa. Deve estar com status QUALIFICADO");
            }
        }

        Docente presidente = docenteRepository.findById(dto.getPresidenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Presidente não encontrado"));

        if (!presidente.isAtivo()) {
            throw new BusinessException("Presidente deve estar ativo");
        }

        Docente secretario = null;
        if (dto.getSecretarioId() != null) {
            secretario = docenteRepository.findById(dto.getSecretarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Secretário não encontrado"));

            if (!secretario.isAtivo()) {
                throw new BusinessException("Secretário deve estar ativo");
            }
        }

        // Validar link virtual para bancas virtuais/híbridas
        if ((dto.getModalidade() != null) &&
            (dto.getModalidade().equals("Virtual") || dto.getModalidade().equals("Híbrida"))) {
            if (dto.getLinkVirtual() == null || dto.getLinkVirtual().isEmpty()) {
                throw new BusinessException("Link virtual é obrigatório para bancas virtuais/híbridas");
            }
        }

        // Criar banca
        Banca banca = bancaMapper.toEntity(dto, trabalho, discente, presidente, secretario);

        banca = bancaRepository.save(banca);

        log.info("Banca agendada com sucesso: {}", banca.getId());
        return bancaMapper.toResponseDTO(banca);
    }

    /**
     * Busca banca por ID.
     *
     * @param id ID da banca
     * @return Banca encontrada
     */
    @Transactional(readOnly = true)
    public BancaResponseDTO buscarPorId(Long id) {
        log.debug("Buscando banca: {}", id);

        Banca banca = bancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        return bancaMapper.toResponseDTO(banca);
    }

    /**
     * Lista todas as bancas com paginação.
     *
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    @Transactional(readOnly = true)
    public Page<BancaResponseDTO> listarTodas(Pageable pageable) {
        log.debug("Listando todas as bancas");

        return bancaRepository.findAll(pageable)
                .map(bancaMapper::toResponseDTO);
    }

    /**
     * Busca bancas por discente.
     *
     * @param discenteId ID do discente
     * @param pageable Parâmetros de paginação
     * @return Página de bancas
     */
    @Transactional(readOnly = true)
    public Page<BancaResponseDTO> buscarPorDiscente(Long discenteId, Pageable pageable) {
        log.debug("Buscando bancas do discente: {}", discenteId);

        return bancaRepository.findByDiscenteId(discenteId, pageable)
                .map(bancaMapper::toResponseDTO);
    }

    /**
     * Busca bancas por trabalho de conclusão.
     *
     * @param trabalhoId ID do trabalho
     * @return Lista de bancas
     */
    @Transactional(readOnly = true)
    public List<BancaResponseDTO> buscarPorTrabalho(Long trabalhoId) {
        log.debug("Buscando bancas do trabalho: {}", trabalhoId);

        return bancaRepository.findByTrabalhoConclusaoId(trabalhoId).stream()
                .map(bancaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca bancas agendadas.
     *
     * @param pageable Parâmetros de paginação
     * @return Página de bancas agendadas
     */
    @Transactional(readOnly = true)
    public Page<BancaResponseDTO> buscarAgendadas(Pageable pageable) {
        log.debug("Buscando bancas agendadas");

        return bancaRepository.findBancasAgendadas(pageable)
                .map(bancaMapper::toResponseDTO);
    }

    /**
     * Atualiza uma banca.
     *
     * @param id ID da banca
     * @param dto Dados para atualização
     * @return Banca atualizada
     */
    @Transactional
    public BancaResponseDTO atualizar(Long id, BancaUpdateDTO dto) {
        log.info("Atualizando banca: {}", id);

        Banca banca = bancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        // Validar se banca pode ser editada
        if (banca.isRealizada()) {
            throw new BusinessException("Banca já realizada não pode ser editada");
        }

        Docente presidente = null;
        if (dto.getPresidenteId() != null) {
            presidente = docenteRepository.findById(dto.getPresidenteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Presidente não encontrado"));

            if (!presidente.isAtivo()) {
                throw new BusinessException("Presidente deve estar ativo");
            }
        }

        Docente secretario = null;
        if (dto.getSecretarioId() != null) {
            secretario = docenteRepository.findById(dto.getSecretarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Secretário não encontrado"));

            if (!secretario.isAtivo()) {
                throw new BusinessException("Secretário deve estar ativo");
            }
        }

        bancaMapper.updateEntity(banca, dto, presidente, secretario);

        banca = bancaRepository.save(banca);

        log.info("Banca atualizada com sucesso: {}", id);
        return bancaMapper.toResponseDTO(banca);
    }

    /**
     * Reagenda uma banca para nova data.
     *
     * @param id ID da banca
     * @param novaData Nova data
     * @return Banca reagendada
     */
    @Transactional
    public BancaResponseDTO reagendar(Long id, LocalDate novaData) {
        log.info("Reagendando banca {} para: {}", id, novaData);

        Banca banca = bancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        if (!banca.podeReagendar()) {
            throw new BusinessException("Banca não pode ser reagendada no status atual");
        }

        if (novaData.isBefore(LocalDate.now())) {
            throw new BusinessException("Nova data deve ser futura");
        }

        banca.setDataAgendada(novaData);
        banca.setStatus("Agendada");

        banca = bancaRepository.save(banca);

        log.info("Banca reagendada com sucesso: {}", id);
        return bancaMapper.toResponseDTO(banca);
    }

    /**
     * Inicia a realização de uma banca.
     *
     * @param id ID da banca
     * @return Banca iniciada
     */
    @Transactional
    public BancaResponseDTO iniciar(Long id) {
        log.info("Iniciando banca: {}", id);

        Banca banca = bancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        if (!banca.podeRealizar()) {
            throw new BusinessException("Banca não pode ser iniciada. Verifique se está agendada e a data é hoje ou já passou");
        }

        // Validar composição da banca antes de iniciar
        validarComposicaoBanca(banca);

        banca.setStatus("Realizada");
        banca.setDataRealizacao(LocalDate.now());

        banca = bancaRepository.save(banca);

        log.info("Banca iniciada com sucesso: {}", id);
        return bancaMapper.toResponseDTO(banca);
    }

    /**
     * Finaliza uma banca com resultado e ata.
     *
     * @param id ID da banca
     * @param resultado Resultado da banca
     * @param ata Ata da banca
     * @return Banca finalizada
     */
    @Transactional
    public BancaResponseDTO finalizar(Long id, ResultadoBanca resultado, Map<String, Object> ata) {
        log.info("Finalizando banca {} com resultado: {}", id, resultado);

        Banca banca = bancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        if (!banca.isRealizada()) {
            throw new BusinessException("Banca deve ser iniciada antes de ser finalizada");
        }

        if (banca.temResultado()) {
            throw new BusinessException("Banca já foi finalizada anteriormente");
        }

        // Validar ata preenchida para aprovação
        if (resultado.isAprovado() && (ata == null || ata.isEmpty())) {
            throw new BusinessException("Ata deve ser preenchida para aprovar candidato");
        }

        banca.setResultado(resultado);
        banca.setAta(ata);

        // Se aprovado, calcular nota final baseada nas notas dos membros
        if (resultado.isAprovado()) {
            calcularNotaFinal(banca);
        }

        banca = bancaRepository.save(banca);

        // Atualizar status do trabalho de conclusão se for defesa
        if (banca.isDefesa() && banca.getTrabalhoConclusao() != null) {
            atualizarStatusTrabalho(banca);
        }

        log.info("Banca finalizada com sucesso: {}", id);
        return bancaMapper.toResponseDTO(banca);
    }

    /**
     * Cancela uma banca agendada.
     *
     * @param id ID da banca
     * @param motivo Motivo do cancelamento
     * @return Banca cancelada
     */
    @Transactional
    public BancaResponseDTO cancelar(Long id, String motivo) {
        log.info("Cancelando banca: {}", id);

        Banca banca = bancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        if (!banca.podeCancelar()) {
            throw new BusinessException("Banca não pode ser cancelada no status atual");
        }

        if (motivo == null || motivo.isEmpty()) {
            throw new BusinessException("Motivo do cancelamento é obrigatório");
        }

        banca.setStatus("Cancelada");
        banca.setObservacoesBanca(motivo);

        banca = bancaRepository.save(banca);

        log.info("Banca cancelada: {}", id);
        return bancaMapper.toResponseDTO(banca);
    }

    /**
     * Adiciona um membro à banca.
     *
     * @param bancaId ID da banca
     * @param membroDTO Dados do membro
     * @return Membro adicionado
     */
    @Transactional
    public MembroBancaResponseDTO adicionarMembro(Long bancaId, MembroBancaCreateDTO membroDTO) {
        log.info("Adicionando membro à banca: {}", bancaId);

        Banca banca = bancaRepository.findById(bancaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        // Validar se banca permite adicionar membros
        if (banca.isRealizada()) {
            throw new BusinessException("Não é possível adicionar membros após a banca ser iniciada");
        }

        // Validar número máximo de membros
        long numeroMembros = membroBancaRepository.countByBancaId(bancaId);
        if (numeroMembros >= 7) {
            throw new BusinessException("Banca já atingiu o número máximo de 7 membros");
        }

        // Validar se membro interno não é duplicado
        if (membroDTO.getDocenteId() != null) {
            if (membroBancaRepository.existsByBancaIdAndDocenteId(bancaId, membroDTO.getDocenteId())) {
                throw new BusinessException("Docente já é membro desta banca");
            }
        }

        // Validar dados do membro externo
        if (membroDTO.getTipo().equals("Externo")) {
            if (membroDTO.getNomeCompleto() == null || membroDTO.getNomeCompleto().isEmpty()) {
                throw new BusinessException("Nome completo é obrigatório para membros externos");
            }
            if (membroDTO.getInstituicao() == null || membroDTO.getInstituicao().isEmpty()) {
                throw new BusinessException("Instituição é obrigatória para membros externos");
            }
        }

        // Validar presidente interno
        if (membroDTO.getFuncao().equals("Presidente") && membroDTO.getTipo().equals("Externo")) {
            throw new BusinessException("Presidente deve ser membro interno");
        }

        // Validar se já existe presidente
        if (membroDTO.getFuncao().equals("Presidente")) {
            if (membroBancaRepository.existsPresidentePorBanca(bancaId)) {
                throw new BusinessException("Banca já possui um presidente definido");
            }
        }

        Docente docente = null;
        if (membroDTO.getDocenteId() != null) {
            docente = docenteRepository.findById(membroDTO.getDocenteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

            if (!docente.isAtivo()) {
                throw new BusinessException("Docente deve estar ativo");
            }
        }

        // Criar membro
        MembroBanca membro = membroBancaMapper.toEntity(membroDTO, banca, docente);

        membro = membroBancaRepository.save(membro);

        log.info("Membro adicionado à banca com sucesso: {}", membro.getId());
        return membroBancaMapper.toResponseDTO(membro);
    }

    /**
     * Remove um membro da banca.
     *
     * @param membroId ID do membro
     */
    @Transactional
    public void removerMembro(Long membroId) {
        log.info("Removendo membro da banca: {}", membroId);

        MembroBanca membro = membroBancaRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        if (!membro.podeSerRemovido()) {
            throw new BusinessException("Membro não pode ser removido após a banca ser iniciada");
        }

        membroBancaRepository.delete(membro);

        log.info("Membro removido da banca: {}", membroId);
    }

    /**
     * Define o presidente da banca.
     *
     * @param bancaId ID da banca
     * @param membroId ID do membro que será presidente
     * @return Banca atualizada
     */
    @Transactional
    public BancaResponseDTO definirPresidente(Long bancaId, Long membroId) {
        log.info("Definindo presidente da banca: {} -> membro: {}", bancaId, membroId);

        Banca banca = bancaRepository.findById(bancaId)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        if (banca.isRealizada()) {
            throw new BusinessException("Não é possível alterar presidente após banca iniciada");
        }

        MembroBanca membro = membroBancaRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        if (!membro.getBanca().getId().equals(bancaId)) {
            throw new BusinessException("Membro não pertence a esta banca");
        }

        if (membro.isExterno()) {
            throw new BusinessException("Presidente deve ser membro interno");
        }

        // Remover função de presidente de outros membros
        Optional<MembroBanca> presidenteAtual = membroBancaRepository.findPresidenteBanca(bancaId);
        if (presidenteAtual.isPresent()) {
            MembroBanca atual = presidenteAtual.get();
            atual.setFuncao("Examinador_Interno");
            membroBancaRepository.save(atual);
        }

        // Definir novo presidente
        membro.setFuncao("Presidente");
        membroBancaRepository.save(membro);

        log.info("Presidente definido com sucesso");
        return bancaMapper.toResponseDTO(banca);
    }

    /**
     * Valida a composição da banca conforme as regras de negócio.
     *
     * @param banca Banca a ser validada
     */
    private void validarComposicaoBanca(Banca banca) {
        log.debug("Validando composição da banca: {}", banca.getId());

        List<MembroBanca> membros = membroBancaRepository.findByBancaId(banca.getId());

        // Validar quantidade mínima e máxima
        int numeroTitulares = (int) membroBancaRepository.countTitularesPorBanca(banca.getId());
        int minimoRequerido = banca.getTipo().getNumeroMinimoMembros();

        if (numeroTitulares < minimoRequerido) {
            throw new BusinessException(String.format(
                "Banca deve ter no mínimo %d membros titulares (não suplentes). Atual: %d",
                minimoRequerido, numeroTitulares
            ));
        }

        if (numeroTitulares > 7) {
            throw new BusinessException("Banca deve ter no máximo 7 membros titulares");
        }

        // Validar membro externo
        long numeroExternos = membroBancaRepository.countMembrosExternosPorBanca(banca.getId());
        if (numeroExternos < 1) {
            throw new BusinessException("Banca deve ter pelo menos 1 membro externo");
        }

        // Validar presidente
        Optional<MembroBanca> presidente = membroBancaRepository.findPresidenteBanca(banca.getId());
        if (presidente.isEmpty()) {
            throw new BusinessException("Banca deve ter um presidente definido");
        }

        // Validar presidente interno
        if (presidente.get().isExterno()) {
            throw new BusinessException("Presidente deve ser membro interno");
        }

        log.debug("Composição da banca validada com sucesso");
    }

    /**
     * Calcula a nota final da banca baseada nas notas dos membros.
     *
     * @param banca Banca para calcular nota final
     */
    private void calcularNotaFinal(Banca banca) {
        List<MembroBanca> membrosComNota = membroBancaRepository.findMembrosComNotaPorBanca(banca.getId());

        if (!membrosComNota.isEmpty()) {
            double media = membrosComNota.stream()
                    .filter(m -> m.getNotaIndividual() != null)
                    .mapToDouble(m -> m.getNotaIndividual().doubleValue())
                    .average()
                    .orElse(0.0);

            banca.setNotaFinal(java.math.BigDecimal.valueOf(media));
            log.debug("Nota final calculada: {}", media);
        }
    }

    /**
     * Atualiza o status do trabalho de conclusão após a banca de defesa.
     *
     * @param banca Banca finalizada
     */
    private void atualizarStatusTrabalho(Banca banca) {
        TrabalhoConclusao trabalho = banca.getTrabalhoConclusao();

        if (banca.isAprovado()) {
            trabalho.setStatus(br.edu.ppg.hub.academic.domain.enums.StatusTrabalho.APROVADO);
            trabalho.setDataDefesa(banca.getDataRealizacao());
            trabalho.setNotaAvaliacao(banca.getNotaFinal());
        } else {
            trabalho.setStatus(br.edu.ppg.hub.academic.domain.enums.StatusTrabalho.DEFENDIDO);
        }

        trabalhoConclusaoRepository.save(trabalho);
        log.debug("Status do trabalho atualizado para: {}", trabalho.getStatus());
    }

    /**
     * Deleta uma banca.
     *
     * @param id ID da banca
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando banca: {}", id);

        Banca banca = bancaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banca não encontrada"));

        if (banca.isRealizada()) {
            throw new BusinessException("Bancas realizadas não podem ser deletadas");
        }

        bancaRepository.delete(banca);

        log.info("Banca deletada com sucesso: {}", id);
    }
}
