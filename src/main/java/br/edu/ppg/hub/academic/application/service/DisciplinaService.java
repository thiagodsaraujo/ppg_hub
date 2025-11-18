package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.disciplina.DisciplinaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.disciplina.DisciplinaMapper;
import br.edu.ppg.hub.academic.application.dto.disciplina.DisciplinaResponseDTO;
import br.edu.ppg.hub.academic.application.dto.disciplina.DisciplinaUpdateDTO;
import br.edu.ppg.hub.academic.domain.enums.StatusDisciplina;
import br.edu.ppg.hub.academic.domain.enums.TipoDisciplina;
import br.edu.ppg.hub.academic.domain.model.Disciplina;
import br.edu.ppg.hub.academic.infrastructure.repository.DisciplinaRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service para operações com Disciplinas
 *
 * @author PPG Hub
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final ProgramaRepository programaRepository;
    private final LinhaPesquisaRepository linhaPesquisaRepository;
    private final DisciplinaMapper disciplinaMapper;

    /**
     * Cria uma nova disciplina
     */
    @Transactional
    public DisciplinaResponseDTO criar(DisciplinaCreateDTO dto) {
        log.info("Criando nova disciplina: {} para programa {}", dto.getCodigo(), dto.getProgramaId());

        // Validar se já existe disciplina com mesmo código no programa
        if (disciplinaRepository.existsByProgramaIdAndCodigo(dto.getProgramaId(), dto.getCodigo())) {
            throw new DuplicateResourceException("Já existe disciplina com o código " + dto.getCodigo() + " neste programa");
        }

        // Buscar programa
        Programa programa = programaRepository.findById(dto.getProgramaId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado"));

        // Validar programa ativo
        if (!programa.isAtivo()) {
            throw new BusinessException("Não é possível criar disciplina em programa inativo");
        }

        // Buscar linha de pesquisa (opcional)
        LinhaPesquisa linhaPesquisa = null;
        if (dto.getLinhaPesquisaId() != null) {
            linhaPesquisa = linhaPesquisaRepository.findById(dto.getLinhaPesquisaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada"));

            // Validar se a linha pertence ao programa
            if (!linhaPesquisa.getPrograma().getId().equals(programa.getId())) {
                throw new BusinessException("Linha de pesquisa não pertence ao programa informado");
            }
        }

        // Validar carga horária
        validarCargaHoraria(dto.getCargaHorariaTotal(), dto.getCargaHorariaTeorica(), dto.getCargaHorariaPratica());

        // Validar créditos
        validarCreditos(dto.getCargaHorariaTotal(), dto.getCreditos());

        // Criar disciplina
        Disciplina disciplina = disciplinaMapper.toEntity(dto, programa, linhaPesquisa);
        disciplina = disciplinaRepository.save(disciplina);

        log.info("Disciplina criada com sucesso: {}", disciplina.getId());
        return disciplinaMapper.toResponseDTO(disciplina);
    }

    /**
     * Atualiza uma disciplina existente
     */
    @Transactional
    public DisciplinaResponseDTO atualizar(Long id, DisciplinaUpdateDTO dto) {
        log.info("Atualizando disciplina: {}", id);

        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        // Validar alteração de código
        if (dto.getCodigo() != null && !dto.getCodigo().equals(disciplina.getCodigo())) {
            if (disciplinaRepository.existsByProgramaIdAndCodigo(disciplina.getPrograma().getId(), dto.getCodigo())) {
                throw new DuplicateResourceException("Já existe disciplina com o código " + dto.getCodigo() + " neste programa");
            }
        }

        // Buscar linha de pesquisa (se alterada)
        LinhaPesquisa linhaPesquisa = null;
        if (dto.getLinhaPesquisaId() != null) {
            linhaPesquisa = linhaPesquisaRepository.findById(dto.getLinhaPesquisaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada"));

            if (!linhaPesquisa.getPrograma().getId().equals(disciplina.getPrograma().getId())) {
                throw new BusinessException("Linha de pesquisa não pertence ao programa da disciplina");
            }
        }

        // Validar carga horária se alterada
        Integer cargaTotal = dto.getCargaHorariaTotal() != null ? dto.getCargaHorariaTotal() : disciplina.getCargaHorariaTotal();
        Integer cargaTeorica = dto.getCargaHorariaTeorica() != null ? dto.getCargaHorariaTeorica() : disciplina.getCargaHorariaTeorica();
        Integer cargaPratica = dto.getCargaHorariaPratica() != null ? dto.getCargaHorariaPratica() : disciplina.getCargaHorariaPratica();
        validarCargaHoraria(cargaTotal, cargaTeorica, cargaPratica);

        // Validar créditos se alterados
        Integer creditos = dto.getCreditos() != null ? dto.getCreditos() : disciplina.getCreditos();
        validarCreditos(cargaTotal, creditos);

        disciplinaMapper.updateEntity(disciplina, dto, linhaPesquisa);
        disciplina = disciplinaRepository.save(disciplina);

        log.info("Disciplina atualizada com sucesso: {}", id);
        return disciplinaMapper.toResponseDTO(disciplina);
    }

    /**
     * Busca disciplina por ID
     */
    @Transactional(readOnly = true)
    public DisciplinaResponseDTO buscarPorId(Long id) {
        log.debug("Buscando disciplina: {}", id);

        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        return disciplinaMapper.toResponseDTO(disciplina);
    }

    /**
     * Busca todas as disciplinas (paginado)
     */
    @Transactional(readOnly = true)
    public Page<DisciplinaResponseDTO> buscarTodas(Pageable pageable) {
        log.debug("Buscando todas as disciplinas");
        return disciplinaRepository.findAll(pageable)
                .map(disciplinaMapper::toResponseDTO);
    }

    /**
     * Busca disciplinas por programa
     */
    @Transactional(readOnly = true)
    public Page<DisciplinaResponseDTO> buscarPorPrograma(Long programaId, Pageable pageable) {
        log.debug("Buscando disciplinas do programa: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        return disciplinaRepository.findByProgramaId(programaId, pageable)
                .map(disciplinaMapper::toResponseDTO);
    }

    /**
     * Busca disciplinas ativas de um programa
     */
    @Transactional(readOnly = true)
    public List<DisciplinaResponseDTO> buscarAtivasPorPrograma(Long programaId) {
        log.debug("Buscando disciplinas ativas do programa: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        return disciplinaRepository.findAtivasByPrograma(programaId).stream()
                .map(disciplinaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca disciplinas por tipo
     */
    @Transactional(readOnly = true)
    public Page<DisciplinaResponseDTO> buscarPorTipo(Long programaId, TipoDisciplina tipo, Pageable pageable) {
        log.debug("Buscando disciplinas do tipo {} no programa {}", tipo, programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        return disciplinaRepository.findByProgramaIdAndTipo(programaId, tipo, pageable)
                .map(disciplinaMapper::toResponseDTO);
    }

    /**
     * Busca disciplinas por status
     */
    @Transactional(readOnly = true)
    public Page<DisciplinaResponseDTO> buscarPorStatus(Long programaId, StatusDisciplina status, Pageable pageable) {
        log.debug("Buscando disciplinas com status {} no programa {}", status, programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        return disciplinaRepository.findByProgramaIdAndStatus(programaId, status, pageable)
                .map(disciplinaMapper::toResponseDTO);
    }

    /**
     * Ativa uma disciplina
     */
    @Transactional
    public DisciplinaResponseDTO ativar(Long id) {
        log.info("Ativando disciplina: {}", id);

        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        if (disciplina.getStatus() == StatusDisciplina.ATIVA) {
            throw new BusinessException("Disciplina já está ativa");
        }

        disciplina.setStatus(StatusDisciplina.ATIVA);
        disciplina = disciplinaRepository.save(disciplina);

        log.info("Disciplina ativada com sucesso: {}", id);
        return disciplinaMapper.toResponseDTO(disciplina);
    }

    /**
     * Desativa uma disciplina
     */
    @Transactional
    public DisciplinaResponseDTO desativar(Long id) {
        log.info("Desativando disciplina: {}", id);

        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        if (disciplina.getStatus() == StatusDisciplina.INATIVA) {
            throw new BusinessException("Disciplina já está inativa");
        }

        disciplina.setStatus(StatusDisciplina.INATIVA);
        disciplina = disciplinaRepository.save(disciplina);

        log.info("Disciplina desativada com sucesso: {}", id);
        return disciplinaMapper.toResponseDTO(disciplina);
    }

    /**
     * Duplica uma disciplina para outro programa
     */
    @Transactional
    public DisciplinaResponseDTO duplicar(Long id, Long novoProgramaId) {
        log.info("Duplicando disciplina {} para programa {}", id, novoProgramaId);

        Disciplina original = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        Programa novoPrograma = programaRepository.findById(novoProgramaId)
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado"));

        if (!novoPrograma.isAtivo()) {
            throw new BusinessException("Não é possível duplicar disciplina para programa inativo");
        }

        // Verificar se o código já existe no novo programa
        if (disciplinaRepository.existsByProgramaIdAndCodigo(novoProgramaId, original.getCodigo())) {
            throw new DuplicateResourceException("Já existe disciplina com o código " + original.getCodigo() + " no programa destino");
        }

        Disciplina nova = Disciplina.builder()
                .programa(novoPrograma)
                .codigo(original.getCodigo())
                .nome(original.getNome())
                .nomeIngles(original.getNomeIngles())
                .ementa(original.getEmenta())
                .ementaIngles(original.getEmentaIngles())
                .objetivos(original.getObjetivos())
                .conteudoProgramatico(original.getConteudoProgramatico())
                .metodologiaEnsino(original.getMetodologiaEnsino())
                .criteriosAvaliacao(original.getCriteriosAvaliacao())
                .bibliografiaBasica(original.getBibliografiaBasica())
                .bibliografiaComplementar(original.getBibliografiaComplementar())
                .cargaHorariaTotal(original.getCargaHorariaTotal())
                .cargaHorariaTeorica(original.getCargaHorariaTeorica())
                .cargaHorariaPratica(original.getCargaHorariaPratica())
                .creditos(original.getCreditos())
                .tipo(original.getTipo())
                .nivel(original.getNivel())
                .modalidade(original.getModalidade())
                .periodicidade(original.getPeriodicidade())
                .maximoAlunos(original.getMaximoAlunos())
                .minimoAlunos(original.getMinimoAlunos())
                .status(StatusDisciplina.ATIVA)
                .build();

        nova = disciplinaRepository.save(nova);

        log.info("Disciplina duplicada com sucesso: {}", nova.getId());
        return disciplinaMapper.toResponseDTO(nova);
    }

    /**
     * Deleta uma disciplina
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando disciplina: {}", id);

        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        // Validar se a disciplina não tem ofertas
        long ofertas = disciplinaRepository.countByProgramaId(disciplina.getPrograma().getId());
        if (ofertas > 0) {
            throw new BusinessException("Não é possível deletar disciplina com ofertas cadastradas");
        }

        disciplinaRepository.delete(disciplina);
        log.info("Disciplina deletada com sucesso: {}", id);
    }

    /**
     * Obtém estatísticas de disciplinas por programa
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEstatisticas(Long programaId) {
        log.debug("Obtendo estatísticas de disciplinas do programa: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", disciplinaRepository.countByProgramaId(programaId));
        stats.put("ativas", disciplinaRepository.countByProgramaIdAndStatus(programaId, StatusDisciplina.ATIVA));
        stats.put("inativas", disciplinaRepository.countByProgramaIdAndStatus(programaId, StatusDisciplina.INATIVA));
        stats.put("obrigatorias", disciplinaRepository.countByProgramaIdAndTipo(programaId, TipoDisciplina.OBRIGATORIA));
        stats.put("eletivas", disciplinaRepository.countByProgramaIdAndTipo(programaId, TipoDisciplina.ELETIVA));
        stats.put("topicosEspeciais", disciplinaRepository.countByProgramaIdAndTipo(programaId, TipoDisciplina.TOPICOS_ESPECIAIS));

        return stats;
    }

    // ===================================
    // Métodos privados de validação
    // ===================================

    /**
     * Valida carga horária (total = teórica + prática)
     */
    private void validarCargaHoraria(Integer total, Integer teorica, Integer pratica) {
        if (total == null) {
            throw new BusinessException("Carga horária total é obrigatória");
        }

        int t = teorica != null ? teorica : 0;
        int p = pratica != null ? pratica : 0;

        if (total != (t + p)) {
            throw new BusinessException("Carga horária total deve ser igual à soma de teórica e prática");
        }
    }

    /**
     * Valida créditos (15 horas = 1 crédito)
     */
    private void validarCreditos(Integer cargaHoraria, Integer creditos) {
        if (creditos == null || cargaHoraria == null) {
            return;
        }

        int creditosCalculados = cargaHoraria / 15;
        if (creditos != creditosCalculados) {
            throw new BusinessException(
                    String.format("Créditos incorretos. Para %d horas, devem ser %d créditos",
                            cargaHoraria, creditosCalculados)
            );
        }
    }
}
