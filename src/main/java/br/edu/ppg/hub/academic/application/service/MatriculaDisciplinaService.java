package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.matricula_disciplina.MatriculaDisciplinaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.matricula_disciplina.MatriculaDisciplinaMapper;
import br.edu.ppg.hub.academic.application.dto.matricula_disciplina.MatriculaDisciplinaResponseDTO;
import br.edu.ppg.hub.academic.domain.enums.StatusMatricula;
import br.edu.ppg.hub.academic.domain.enums.StatusOferta;
import br.edu.ppg.hub.academic.domain.model.Discente;
import br.edu.ppg.hub.academic.domain.model.MatriculaDisciplina;
import br.edu.ppg.hub.academic.domain.model.OfertaDisciplina;
import br.edu.ppg.hub.academic.infrastructure.repository.DiscenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.MatriculaDisciplinaRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.OfertaDisciplinaRepository;
import br.edu.ppg.hub.shared.exception.BusinessException;
import br.edu.ppg.hub.shared.exception.ConflictException;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service para operações com Matrículas em Disciplinas
 *
 * Implementa controle de concorrência com lock pessimista para evitar race conditions
 * no processo de matrícula quando múltiplos alunos tentam se matricular simultaneamente.
 *
 * @author PPG Hub
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatriculaDisciplinaService {

    private final MatriculaDisciplinaRepository matriculaRepository;
    private final OfertaDisciplinaRepository ofertaDisciplinaRepository;
    private final DiscenteRepository discenteRepository;
    private final MatriculaDisciplinaMapper matriculaMapper;

    /**
     * Matricula um discente em uma oferta de disciplina
     *
     * USA LOCK PESSIMISTA para evitar race condition nas vagas
     */
    @Transactional
    public MatriculaDisciplinaResponseDTO matricular(MatriculaDisciplinaCreateDTO dto) {
        log.info("Matriculando discente {} na oferta {}", dto.getDiscenteId(), dto.getOfertaDisciplinaId());

        // 1. Buscar oferta com LOCK PESSIMISTA (WRITE) para controle de concorrência
        OfertaDisciplina oferta = ofertaDisciplinaRepository
                .findByIdForUpdate(dto.getOfertaDisciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        // 2. Validar status da oferta
        if (!oferta.isInscricoesAbertas()) {
            throw new BusinessException("As inscrições não estão abertas para esta oferta");
        }

        // 3. Validar vagas disponíveis (proteção contra race condition)
        if (oferta.getVagasOcupadas() >= oferta.getVagasOferecidas()) {
            throw new BusinessException("Não há vagas disponíveis para esta oferta");
        }

        // 4. Buscar discente
        Discente discente = discenteRepository.findById(dto.getDiscenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Discente não encontrado"));

        // Validar discente ativo
        if (!discente.isAtivo()) {
            throw new BusinessException("Discente não está ativo no programa");
        }

        // Validar se discente pertence ao mesmo programa da disciplina
        if (!discente.getPrograma().getId().equals(oferta.getDisciplina().getPrograma().getId())) {
            throw new BusinessException("Discente não pertence ao programa da disciplina");
        }

        // 5. Validar duplicação (evitar matrícula dupla)
        if (matriculaRepository.existsByDiscenteIdAndOfertaDisciplinaId(dto.getDiscenteId(), dto.getOfertaDisciplinaId())) {
            throw new ConflictException("Discente já está matriculado nesta oferta");
        }

        // 6. Criar matrícula
        MatriculaDisciplina matricula = matriculaMapper.toEntity(dto, oferta, discente);
        matricula = matriculaRepository.save(matricula);

        // 7. Incrementar contador de vagas ocupadas (dentro da mesma transação)
        oferta.incrementarVagasOcupadas();
        ofertaDisciplinaRepository.save(oferta);

        log.info("Matrícula realizada com sucesso: {} - Vagas restantes: {}",
                matricula.getId(), oferta.calcularVagasDisponiveis());

        return matriculaMapper.toResponseDTO(matricula);
    }

    /**
     * Busca matrícula por ID
     */
    @Transactional(readOnly = true)
    public MatriculaDisciplinaResponseDTO buscarPorId(Long id) {
        log.debug("Buscando matrícula: {}", id);

        MatriculaDisciplina matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

        return matriculaMapper.toResponseDTO(matricula);
    }

    /**
     * Busca todas as matrículas de um discente
     */
    @Transactional(readOnly = true)
    public Page<MatriculaDisciplinaResponseDTO> buscarPorDiscente(Long discenteId, Pageable pageable) {
        log.debug("Buscando matrículas do discente: {}", discenteId);

        if (!discenteRepository.existsById(discenteId)) {
            throw new ResourceNotFoundException("Discente não encontrado");
        }

        return matriculaRepository.findByDiscenteId(discenteId, pageable)
                .map(matriculaMapper::toResponseDTO);
    }

    /**
     * Busca todas as matrículas de uma oferta
     */
    @Transactional(readOnly = true)
    public Page<MatriculaDisciplinaResponseDTO> buscarPorOferta(Long ofertaId, Pageable pageable) {
        log.debug("Buscando matrículas da oferta: {}", ofertaId);

        if (!ofertaDisciplinaRepository.existsById(ofertaId)) {
            throw new ResourceNotFoundException("Oferta de disciplina não encontrada");
        }

        return matriculaRepository.findByOfertaDisciplinaId(ofertaId, pageable)
                .map(matriculaMapper::toResponseDTO);
    }

    /**
     * Busca histórico completo de matrículas de um discente
     */
    @Transactional(readOnly = true)
    public List<MatriculaDisciplinaResponseDTO> getHistoricoMatriculas(Long discenteId) {
        log.debug("Buscando histórico de matrículas do discente: {}", discenteId);

        if (!discenteRepository.existsById(discenteId)) {
            throw new ResourceNotFoundException("Discente não encontrado");
        }

        return matriculaRepository.findHistoricoByDiscenteId(discenteId).stream()
                .map(matriculaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tranca uma matrícula
     */
    @Transactional
    public MatriculaDisciplinaResponseDTO trancar(Long id) {
        log.info("Trancando matrícula: {}", id);

        MatriculaDisciplina matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

        if (!matricula.podeTranscar()) {
            throw new BusinessException("Matrícula não pode ser trancada no estado atual");
        }

        // Validar se a oferta permite trancamento
        if (matricula.getOfertaDisciplina().getStatus() == StatusOferta.CONCLUIDA) {
            throw new BusinessException("Não é possível trancar matrícula em oferta concluída");
        }

        matricula.trancar();
        matricula = matriculaRepository.save(matricula);

        // Decrementar vagas ocupadas
        OfertaDisciplina oferta = matricula.getOfertaDisciplina();
        oferta.decrementarVagasOcupadas();
        ofertaDisciplinaRepository.save(oferta);

        log.info("Matrícula trancada com sucesso: {}", id);
        return matriculaMapper.toResponseDTO(matricula);
    }

    /**
     * Lança nota para uma avaliação
     */
    @Transactional
    public MatriculaDisciplinaResponseDTO lancarNota(Long id, BigDecimal nota) {
        log.info("Lançando nota para matrícula: {}", id);

        MatriculaDisciplina matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

        // Validar matrícula ativa
        if (!matricula.isAtiva()) {
            throw new BusinessException("Matrícula não está ativa");
        }

        // Validar oferta permite lançar notas
        if (!matricula.getOfertaDisciplina().permiteLancarNotas()) {
            throw new BusinessException("Oferta não está em período de lançamento de notas");
        }

        // Validar nota
        if (nota == null || nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(new BigDecimal("10.0")) > 0) {
            throw new BusinessException("Nota deve estar entre 0 e 10");
        }

        matricula.setNotaFinalComArredondamento(nota);
        matricula = matriculaRepository.save(matricula);

        log.info("Nota lançada com sucesso para matrícula: {}", id);
        return matriculaMapper.toResponseDTO(matricula);
    }

    /**
     * Lança frequência
     */
    @Transactional
    public MatriculaDisciplinaResponseDTO lancarFrequencia(Long id, BigDecimal frequencia) {
        log.info("Lançando frequência para matrícula: {}", id);

        MatriculaDisciplina matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

        // Validar matrícula ativa
        if (!matricula.isAtiva()) {
            throw new BusinessException("Matrícula não está ativa");
        }

        // Validar oferta permite lançar frequência
        if (!matricula.getOfertaDisciplina().permiteLancarNotas()) {
            throw new BusinessException("Oferta não está em período de lançamento de frequência");
        }

        // Validar frequência (0 a 100)
        if (frequencia == null || frequencia.compareTo(BigDecimal.ZERO) < 0 ||
            frequencia.compareTo(new BigDecimal("100.0")) > 0) {
            throw new BusinessException("Frequência deve estar entre 0 e 100");
        }

        matricula.setFrequenciaComArredondamento(frequencia);
        matricula = matriculaRepository.save(matricula);

        log.info("Frequência lançada com sucesso para matrícula: {}", id);
        return matriculaMapper.toResponseDTO(matricula);
    }

    /**
     * Calcula resultado final (aprovar/reprovar)
     *
     * Critérios: nota >= 7.0 E frequência >= 75%
     */
    @Transactional
    public MatriculaDisciplinaResponseDTO calcularResultado(Long id) {
        log.info("Calculando resultado para matrícula: {}", id);

        MatriculaDisciplina matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

        // Validar matrícula ativa
        if (!matricula.isAtiva()) {
            throw new BusinessException("Matrícula não está ativa");
        }

        // Validar se possui nota e frequência
        if (matricula.getNotaFinal() == null) {
            throw new BusinessException("Nota final não foi lançada");
        }
        if (matricula.getFrequenciaPercentual() == null) {
            throw new BusinessException("Frequência não foi lançada");
        }

        // Calcular resultado
        String resultado = matricula.calcularResultadoFinal();
        matricula = matriculaRepository.save(matricula);

        log.info("Resultado calculado para matrícula {}: {}", id, resultado);
        return matriculaMapper.toResponseDTO(matricula);
    }

    /**
     * Calcula resultado em lote para todas as matrículas de uma oferta
     */
    @Transactional
    public Map<String, Object> calcularResultadosOferta(Long ofertaId) {
        log.info("Calculando resultados em lote para oferta: {}", ofertaId);

        if (!ofertaDisciplinaRepository.existsById(ofertaId)) {
            throw new ResourceNotFoundException("Oferta de disciplina não encontrada");
        }

        List<MatriculaDisciplina> matriculas = matriculaRepository.findParaLancamentoResultado(ofertaId);

        int processadas = 0;
        int aprovados = 0;
        int reprovados = 0;
        int erros = 0;

        for (MatriculaDisciplina matricula : matriculas) {
            try {
                matricula.calcularResultadoFinal();
                matriculaRepository.save(matricula);
                processadas++;

                if (matricula.isAprovado()) {
                    aprovados++;
                } else {
                    reprovados++;
                }
            } catch (Exception e) {
                log.error("Erro ao calcular resultado da matrícula {}: {}", matricula.getId(), e.getMessage());
                erros++;
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ofertaId", ofertaId);
        resultado.put("totalProcessadas", processadas);
        resultado.put("aprovados", aprovados);
        resultado.put("reprovados", reprovados);
        resultado.put("erros", erros);

        log.info("Resultados calculados: {} processadas, {} aprovados, {} reprovados, {} erros",
                processadas, aprovados, reprovados, erros);

        return resultado;
    }

    /**
     * Obtém estatísticas gerais de um discente
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEstatisticasDiscente(Long discenteId) {
        log.debug("Obtendo estatísticas do discente: {}", discenteId);

        if (!discenteRepository.existsById(discenteId)) {
            throw new ResourceNotFoundException("Discente não encontrado");
        }

        Object[] stats = matriculaRepository.calcularEstatisticasDiscente(discenteId);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("discenteId", discenteId);

        if (stats != null && stats.length >= 5) {
            resultado.put("totalDisciplinas", stats[0]);
            resultado.put("aprovadas", stats[1]);
            resultado.put("reprovadas", stats[2]);
            resultado.put("creditosObtidos", stats[3]);
            resultado.put("mediaGeral", stats[4]);

            // Calcular taxa de aprovação
            Long total = (Long) stats[0];
            Long aprovadas = (Long) stats[1];
            if (total > 0) {
                double taxaAprovacao = (aprovadas.doubleValue() / total.doubleValue()) * 100;
                resultado.put("taxaAprovacao", String.format("%.2f%%", taxaAprovacao));
            }
        }

        return resultado;
    }

    /**
     * Deleta uma matrícula (só se permitido)
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando matrícula: {}", id);

        MatriculaDisciplina matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

        // Validar se pode deletar
        if (matricula.getOfertaDisciplina().getStatus() == StatusOferta.CONCLUIDA) {
            throw new BusinessException("Não é possível deletar matrícula de oferta concluída");
        }

        if (matricula.getSituacao() != StatusMatricula.MATRICULADO) {
            throw new BusinessException("Só é possível deletar matrículas no status 'Matriculado'");
        }

        // Decrementar vagas ocupadas se estiver matriculado
        if (matricula.isAtiva()) {
            OfertaDisciplina oferta = matricula.getOfertaDisciplina();
            oferta.decrementarVagasOcupadas();
            ofertaDisciplinaRepository.save(oferta);
        }

        matriculaRepository.delete(matricula);
        log.info("Matrícula deletada com sucesso: {}", id);
    }
}
