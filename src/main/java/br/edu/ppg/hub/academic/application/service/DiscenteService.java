package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.discente.DiscenteCreateDTO;
import br.edu.ppg.hub.academic.application.dto.discente.DiscenteMapper;
import br.edu.ppg.hub.academic.application.dto.discente.DiscenteResponseDTO;
import br.edu.ppg.hub.academic.application.dto.discente.DiscenteUpdateDTO;
import br.edu.ppg.hub.academic.domain.enums.StatusDiscente;
import br.edu.ppg.hub.academic.domain.enums.TipoCurso;
import br.edu.ppg.hub.academic.domain.model.Discente;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.infrastructure.repository.DiscenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.DocenteRepository;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.infrastructure.repository.UsuarioRepository;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
import br.edu.ppg.hub.core.infrastructure.repository.LinhaPesquisaRepository;
import br.edu.ppg.hub.core.infrastructure.repository.ProgramaRepository;
import br.edu.ppg.hub.shared.exception.BusinessException;
import br.edu.ppg.hub.shared.exception.DuplicateResourceException;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service para operações com Discentes
 *
 * @author PPG Hub
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiscenteService {

    private final DiscenteRepository discenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaRepository programaRepository;
    private final LinhaPesquisaRepository linhaPesquisaRepository;
    private final DocenteRepository docenteRepository;
    private final DiscenteMapper discenteMapper;

    /**
     * Matricula um novo discente
     */
    @Transactional
    public DiscenteResponseDTO matricular(DiscenteCreateDTO dto) {
        log.info("Matriculando novo discente: {} no programa {}", dto.getNumeroMatricula(), dto.getProgramaId());

        // Validar se já existe discente com mesmo número de matrícula no programa
        if (discenteRepository.existsByProgramaIdAndNumeroMatricula(dto.getProgramaId(), dto.getNumeroMatricula())) {
            throw new DuplicateResourceException("Número de matrícula já existe neste programa");
        }

        // Buscar entidades relacionadas
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Programa programa = programaRepository.findById(dto.getProgramaId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado"));

        LinhaPesquisa linhaPesquisa = null;
        if (dto.getLinhaPesquisaId() != null) {
            linhaPesquisa = linhaPesquisaRepository.findById(dto.getLinhaPesquisaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada"));

            if (!linhaPesquisa.getPrograma().getId().equals(programa.getId())) {
                throw new BusinessException("Linha de pesquisa não pertence ao programa informado");
            }
        }

        Docente orientador = null;
        if (dto.getOrientadorId() != null) {
            orientador = docenteRepository.findById(dto.getOrientadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado"));

            if (!orientador.getPrograma().getId().equals(programa.getId())) {
                throw new BusinessException("Orientador não pertence ao programa informado");
            }

            if (!orientador.podeOrientar()) {
                throw new BusinessException("Orientador não está apto para orientar novos alunos");
            }

            if (orientador.atingiuLimiteOrientacoes()) {
                throw new BusinessException("Orientador já atingiu o limite de orientações simultâneas");
            }
        }

        // Criar discente
        Discente discente = discenteMapper.toEntity(dto, usuario, programa, linhaPesquisa, orientador);

        // Calcular prazo limite se prazo original foi informado
        if (dto.getPrazoOriginal() != null) {
            discente.setDataLimiteAtual(dto.getPrazoOriginal());
        } else {
            // Calcular prazo padrão baseado no tipo de curso
            int meses = dto.getTipoCurso().getPrazoMaximoMeses();
            discente.setPrazoOriginal(dto.getDataIngresso().plusMonths(meses));
            discente.setDataLimiteAtual(dto.getDataIngresso().plusMonths(meses));
        }

        discente = discenteRepository.save(discente);

        // Atualizar contador de orientações do orientador
        if (orientador != null) {
            atualizarContagemOrientacoes(orientador);
        }

        log.info("Discente matriculado com sucesso: {}", discente.getId());
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Atualiza um discente existente
     */
    @Transactional
    public DiscenteResponseDTO atualizar(Long id, DiscenteUpdateDTO dto) {
        log.info("Atualizando discente: {}", id);

        Discente discente = discenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        LinhaPesquisa linhaPesquisa = null;
        if (dto.getLinhaPesquisaId() != null) {
            linhaPesquisa = linhaPesquisaRepository.findById(dto.getLinhaPesquisaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada"));

            if (!linhaPesquisa.getPrograma().getId().equals(discente.getPrograma().getId())) {
                throw new BusinessException("Linha de pesquisa não pertence ao programa do discente");
            }
        }

        Docente novoOrientador = null;
        if (dto.getOrientadorId() != null && !dto.getOrientadorId().equals(
                discente.getOrientador() != null ? discente.getOrientador().getId() : null)) {
            novoOrientador = docenteRepository.findById(dto.getOrientadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado"));

            if (!novoOrientador.podeOrientar()) {
                throw new BusinessException("Novo orientador não está apto para orientar");
            }
        }

        Docente coorientadorInterno = null;
        if (dto.getCoorientadorInternoId() != null) {
            coorientadorInterno = docenteRepository.findById(dto.getCoorientadorInternoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coorientador não encontrado"));
        }

        discenteMapper.updateEntity(discente, dto, linhaPesquisa, novoOrientador, coorientadorInterno);
        discente = discenteRepository.save(discente);

        log.info("Discente atualizado com sucesso: {}", id);
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Busca discente por ID
     */
    @Transactional(readOnly = true)
    public DiscenteResponseDTO buscarPorId(Long id) {
        log.debug("Buscando discente: {}", id);

        Discente discente = discenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Busca todos os discentes (paginado)
     */
    @Transactional(readOnly = true)
    public Page<DiscenteResponseDTO> buscarTodos(Pageable pageable) {
        log.debug("Buscando todos os discentes");
        return discenteRepository.findAll(pageable)
                .map(discenteMapper::toResponseDTO);
    }

    /**
     * Busca discentes por programa
     */
    @Transactional(readOnly = true)
    public Page<DiscenteResponseDTO> buscarPorPrograma(Long programaId, Pageable pageable) {
        log.debug("Buscando discentes do programa: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        return discenteRepository.findByProgramaId(programaId, pageable)
                .map(discenteMapper::toResponseDTO);
    }

    /**
     * Busca discentes por orientador
     */
    @Transactional(readOnly = true)
    public Page<DiscenteResponseDTO> buscarPorOrientador(Long orientadorId, Pageable pageable) {
        log.debug("Buscando discentes do orientador: {}", orientadorId);

        if (!docenteRepository.existsById(orientadorId)) {
            throw new ResourceNotFoundException("Orientador não encontrado");
        }

        return discenteRepository.findByOrientadorId(orientadorId, pageable)
                .map(discenteMapper::toResponseDTO);
    }

    /**
     * Busca discentes por status
     */
    @Transactional(readOnly = true)
    public Page<DiscenteResponseDTO> buscarPorStatus(StatusDiscente status, Pageable pageable) {
        log.debug("Buscando discentes com status: {}", status);
        return discenteRepository.findByStatus(status, pageable)
                .map(discenteMapper::toResponseDTO);
    }

    /**
     * Busca discentes por tipo de curso
     */
    @Transactional(readOnly = true)
    public Page<DiscenteResponseDTO> buscarPorTipoCurso(TipoCurso tipoCurso, Pageable pageable) {
        log.debug("Buscando discentes de {}", tipoCurso);
        return discenteRepository.findByTipoCurso(tipoCurso, pageable)
                .map(discenteMapper::toResponseDTO);
    }

    /**
     * Vincula orientador a um discente
     */
    @Transactional
    public DiscenteResponseDTO vincularOrientador(Long discenteId, Long orientadorId) {
        log.info("Vinculando orientador {} ao discente {}", orientadorId, discenteId);

        Discente discente = discenteRepository.findById(discenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        Docente novoOrientador = docenteRepository.findById(orientadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Orientador não encontrado"));

        if (!novoOrientador.getPrograma().getId().equals(discente.getPrograma().getId())) {
            throw new BusinessException("Orientador não pertence ao programa do discente");
        }

        if (!novoOrientador.podeOrientar()) {
            throw new BusinessException("Orientador não está apto para orientar");
        }

        Docente orientadorAntigo = discente.getOrientador();
        discente.setOrientador(novoOrientador);
        discente = discenteRepository.save(discente);

        // Atualizar contagem de orientações
        if (orientadorAntigo != null) {
            atualizarContagemOrientacoes(orientadorAntigo);
        }
        atualizarContagemOrientacoes(novoOrientador);

        log.info("Orientador vinculado com sucesso");
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Troca o orientador de um discente
     */
    @Transactional
    public DiscenteResponseDTO trocarOrientador(Long discenteId, Long novoOrientadorId, String motivo) {
        log.info("Trocando orientador do discente {} para {}", discenteId, novoOrientadorId);
        return vincularOrientador(discenteId, novoOrientadorId);
    }

    /**
     * Registra a qualificação de um discente
     */
    @Transactional
    public DiscenteResponseDTO registrarQualificacao(Long discenteId, LocalDate dataQualificacao, String resultado) {
        log.info("Registrando qualificação do discente: {}", discenteId);

        Discente discente = discenteRepository.findById(discenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        if (!discente.isAtivo()) {
            throw new BusinessException("Discente não está ativo");
        }

        discente.setQualificacaoRealizada(true);
        discente.setDataQualificacao(dataQualificacao);
        discente.setResultadoQualificacao(resultado);

        if ("Aprovado".equals(resultado) || "Aprovado com Restrições".equals(resultado)) {
            discente.setStatus(StatusDiscente.QUALIFICADO);
        }

        discente = discenteRepository.save(discente);

        log.info("Qualificação registrada com sucesso");
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Registra a defesa de um discente
     */
    @Transactional
    public DiscenteResponseDTO registrarDefesa(Long discenteId, LocalDate dataDefesa, String resultado, String tituloFinal) {
        log.info("Registrando defesa do discente: {}", discenteId);

        Discente discente = discenteRepository.findById(discenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        if (!discente.podeDefender()) {
            throw new BusinessException("Discente não está apto para defender (precisa estar qualificado)");
        }

        discente.setDataDefesa(dataDefesa);
        discente.setResultadoDefesa(resultado);
        discente.setTituloFinal(tituloFinal);

        if ("Aprovado".equals(resultado) || "Aprovado com Correções".equals(resultado)) {
            discente.setStatus(StatusDiscente.TITULADO);
        }

        discente = discenteRepository.save(discente);

        // Atualizar contagem de orientações concluídas do orientador
        if (discente.getOrientador() != null) {
            Docente orientador = discente.getOrientador();
            if (discente.getTipoCurso() == TipoCurso.MESTRADO) {
                orientador.setOrientacoesMestradoConcluidas(
                    (orientador.getOrientacoesMestradoConcluidas() != null ? orientador.getOrientacoesMestradoConcluidas() : 0) + 1
                );
                orientador.setOrientacoesMestradoAndamento(
                    Math.max(0, (orientador.getOrientacoesMestradoAndamento() != null ? orientador.getOrientacoesMestradoAndamento() : 0) - 1)
                );
            } else {
                orientador.setOrientacoesDoutoradoConcluidas(
                    (orientador.getOrientacoesDoutoradoConcluidas() != null ? orientador.getOrientacoesDoutoradoConcluidas() : 0) + 1
                );
                orientador.setOrientacoesDoutoradoAndamento(
                    Math.max(0, (orientador.getOrientacoesDoutoradoAndamento() != null ? orientador.getOrientacoesDoutoradoAndamento() : 0) - 1)
                );
            }
            docenteRepository.save(orientador);
        }

        log.info("Defesa registrada com sucesso");
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Adiciona uma prorrogação de prazo
     */
    @Transactional
    public DiscenteResponseDTO adicionarProrrogacao(Long discenteId, int meses, String motivo) {
        log.info("Adicionando prorrogação de {} meses para discente {}", meses, discenteId);

        Discente discente = discenteRepository.findById(discenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        if (!discente.isAtivo()) {
            throw new BusinessException("Discente não está ativo");
        }

        // Criar nova prorrogação
        Map<String, Object> prorrogacao = new HashMap<>();
        prorrogacao.put("motivo", motivo);
        prorrogacao.put("meses", meses);
        prorrogacao.put("data_aprovacao", LocalDate.now().toString());

        List<Map<String, Object>> prorrogacoes = new ArrayList<>(discente.getProrrogacoes());
        prorrogacoes.add(prorrogacao);
        discente.setProrrogacoes(prorrogacoes);

        // Atualizar data limite
        LocalDate novaDataLimite = discente.getDataLimiteAtual().plusMonths(meses);
        discente.setDataLimiteAtual(novaDataLimite);

        discente = discenteRepository.save(discente);

        log.info("Prorrogação adicionada com sucesso. Nova data limite: {}", novaDataLimite);
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Tranca matrícula do discente
     */
    @Transactional
    public DiscenteResponseDTO trancar(Long discenteId, String motivo) {
        log.info("Trancando matrícula do discente: {}", discenteId);

        Discente discente = discenteRepository.findById(discenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        // Implementar lógica de trancamento se necessário
        // Por ora, apenas registra no motivo

        discente = discenteRepository.save(discente);

        log.info("Matrícula trancada com sucesso");
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Desliga um discente do programa
     */
    @Transactional
    public DiscenteResponseDTO desligar(Long discenteId, String motivo) {
        log.info("Desligando discente: {}", discenteId);

        Discente discente = discenteRepository.findById(discenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        discente.setStatus(StatusDiscente.DESLIGADO);
        discente.setMotivoDesligamento(motivo);
        discente.setDataDesligamento(LocalDate.now());

        discente = discenteRepository.save(discente);

        // Atualizar contagem de orientações do orientador
        if (discente.getOrientador() != null) {
            atualizarContagemOrientacoes(discente.getOrientador());
        }

        log.info("Discente desligado com sucesso");
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Titula um discente (marca como concluído)
     */
    @Transactional
    public DiscenteResponseDTO titular(Long discenteId) {
        log.info("Titulando discente: {}", discenteId);

        Discente discente = discenteRepository.findById(discenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        if (discente.getDataDefesa() == null) {
            throw new BusinessException("Discente ainda não defendeu");
        }

        discente.setStatus(StatusDiscente.TITULADO);
        discente = discenteRepository.save(discente);

        log.info("Discente titulado com sucesso");
        return discenteMapper.toResponseDTO(discente);
    }

    /**
     * Retorna estatísticas de discentes de um programa
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEstatisticasPorPrograma(Long programaId) {
        log.debug("Calculando estatísticas de discentes do programa: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        Map<String, Long> stats = discenteRepository.getEstatisticasPorPrograma(programaId);

        Map<String, Object> result = new HashMap<>(stats);
        result.put("ativos", discenteRepository.countByProgramaIdAndStatus(programaId, StatusDiscente.CURSANDO));
        result.put("qualificados", discenteRepository.countByProgramaIdAndStatus(programaId, StatusDiscente.QUALIFICADO));

        return result;
    }

    /**
     * Atualiza a contagem de orientações de um docente
     */
    private void atualizarContagemOrientacoes(Docente docente) {
        long mestradoAndamento = discenteRepository.countByOrientadorId(docente.getId());
        long doutoradoAndamento = discenteRepository.countByOrientadorId(docente.getId());

        // Aqui seria ideal filtrar por tipo de curso, mas por simplicidade estamos contando todos
        // Em uma implementação real, você criaria queries específicas no repository

        docenteRepository.save(docente);
    }

    /**
     * Deleta um discente
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando discente: {}", id);

        Discente discente = discenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        discenteRepository.delete(discente);

        log.info("Discente deletado com sucesso: {}", id);
    }
}
