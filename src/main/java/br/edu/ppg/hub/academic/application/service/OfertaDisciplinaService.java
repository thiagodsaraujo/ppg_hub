package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.oferta_disciplina.OfertaDisciplinaCreateDTO;
import br.edu.ppg.hub.academic.application.dto.oferta_disciplina.OfertaDisciplinaMapper;
import br.edu.ppg.hub.academic.application.dto.oferta_disciplina.OfertaDisciplinaResponseDTO;
import br.edu.ppg.hub.academic.application.dto.oferta_disciplina.OfertaDisciplinaUpdateDTO;
import br.edu.ppg.hub.academic.domain.enums.StatusOferta;
import br.edu.ppg.hub.academic.domain.model.Disciplina;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.OfertaDisciplina;
import br.edu.ppg.hub.academic.infrastructure.repository.DisciplinaRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.DocenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.MatriculaDisciplinaRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.OfertaDisciplinaRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service para operações com Ofertas de Disciplinas
 *
 * @author PPG Hub
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OfertaDisciplinaService {

    private final OfertaDisciplinaRepository ofertaDisciplinaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final DocenteRepository docenteRepository;
    private final MatriculaDisciplinaRepository matriculaRepository;
    private final OfertaDisciplinaMapper ofertaDisciplinaMapper;

    /**
     * Cria uma nova oferta de disciplina
     */
    @Transactional
    public OfertaDisciplinaResponseDTO criar(OfertaDisciplinaCreateDTO dto) {
        log.info("Criando nova oferta da disciplina {} para período {}", dto.getDisciplinaId(), dto.getPeriodo());

        // Validar se já existe oferta para a mesma disciplina, ano, semestre e turma
        if (ofertaDisciplinaRepository.existsByDisciplinaIdAndAnoAndSemestreAndTurma(
                dto.getDisciplinaId(), dto.getAno(), dto.getSemestre(), dto.getTurma())) {
            throw new DuplicateResourceException("Já existe oferta desta disciplina para este período e turma");
        }

        // Buscar disciplina
        Disciplina disciplina = disciplinaRepository.findById(dto.getDisciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        // Validar disciplina ativa
        if (!disciplina.isAtiva()) {
            throw new BusinessException("Não é possível criar oferta de disciplina inativa");
        }

        // Buscar docente responsável
        Docente docenteResponsavel = docenteRepository.findById(dto.getDocenteResponsavelId())
                .orElseThrow(() -> new ResourceNotFoundException("Docente responsável não encontrado"));

        // Validar docente ativo
        if (!docenteResponsavel.isAtivo()) {
            throw new BusinessException("Docente responsável não está ativo");
        }

        // Validar se docente pertence ao mesmo programa da disciplina
        if (!docenteResponsavel.getPrograma().getId().equals(disciplina.getPrograma().getId())) {
            throw new BusinessException("Docente responsável não pertence ao programa da disciplina");
        }

        // Buscar docente colaborador (opcional)
        Docente docenteColaborador = null;
        if (dto.getDocenteColaboradorId() != null) {
            docenteColaborador = docenteRepository.findById(dto.getDocenteColaboradorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Docente colaborador não encontrado"));

            if (!docenteColaborador.isAtivo()) {
                throw new BusinessException("Docente colaborador não está ativo");
            }
        }

        // Validar datas
        if (!dto.getDataFim().isAfter(dto.getDataInicio())) {
            throw new BusinessException("Data de fim deve ser posterior à data de início");
        }

        // Criar oferta
        OfertaDisciplina oferta = ofertaDisciplinaMapper.toEntity(dto, disciplina, docenteResponsavel, docenteColaborador);
        oferta = ofertaDisciplinaRepository.save(oferta);

        log.info("Oferta de disciplina criada com sucesso: {}", oferta.getId());
        return ofertaDisciplinaMapper.toResponseDTO(oferta);
    }

    /**
     * Atualiza uma oferta de disciplina existente
     */
    @Transactional
    public OfertaDisciplinaResponseDTO atualizar(Long id, OfertaDisciplinaUpdateDTO dto) {
        log.info("Atualizando oferta de disciplina: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        // Validar se oferta pode ser atualizada
        if (oferta.getStatus() == StatusOferta.CONCLUIDA) {
            throw new BusinessException("Não é possível atualizar oferta concluída");
        }

        // Buscar docente responsável (se alterado)
        Docente docenteResponsavel = null;
        if (dto.getDocenteResponsavelId() != null) {
            docenteResponsavel = docenteRepository.findById(dto.getDocenteResponsavelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Docente responsável não encontrado"));

            if (!docenteResponsavel.isAtivo()) {
                throw new BusinessException("Docente responsável não está ativo");
            }

            if (!docenteResponsavel.getPrograma().getId().equals(oferta.getDisciplina().getPrograma().getId())) {
                throw new BusinessException("Docente responsável não pertence ao programa da disciplina");
            }
        }

        // Buscar docente colaborador (se alterado)
        Docente docenteColaborador = null;
        if (dto.getDocenteColaboradorId() != null) {
            docenteColaborador = docenteRepository.findById(dto.getDocenteColaboradorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Docente colaborador não encontrado"));

            if (!docenteColaborador.isAtivo()) {
                throw new BusinessException("Docente colaborador não está ativo");
            }
        }

        // Validar datas se alteradas
        LocalDate dataInicio = dto.getDataInicio() != null ? dto.getDataInicio() : oferta.getDataInicio();
        LocalDate dataFim = dto.getDataFim() != null ? dto.getDataFim() : oferta.getDataFim();
        if (!dataFim.isAfter(dataInicio)) {
            throw new BusinessException("Data de fim deve ser posterior à data de início");
        }

        // Validar alteração de vagas
        if (dto.getVagasOferecidas() != null && dto.getVagasOferecidas() < oferta.getVagasOcupadas()) {
            throw new BusinessException("Não é possível reduzir vagas para menos que o número de vagas ocupadas");
        }

        ofertaDisciplinaMapper.updateEntity(oferta, dto, docenteResponsavel, docenteColaborador);
        oferta = ofertaDisciplinaRepository.save(oferta);

        log.info("Oferta de disciplina atualizada com sucesso: {}", id);
        return ofertaDisciplinaMapper.toResponseDTO(oferta);
    }

    /**
     * Busca oferta por ID
     */
    @Transactional(readOnly = true)
    public OfertaDisciplinaResponseDTO buscarPorId(Long id) {
        log.debug("Buscando oferta de disciplina: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        return ofertaDisciplinaMapper.toResponseDTO(oferta);
    }

    /**
     * Busca todas as ofertas (paginado)
     */
    @Transactional(readOnly = true)
    public Page<OfertaDisciplinaResponseDTO> buscarTodas(Pageable pageable) {
        log.debug("Buscando todas as ofertas de disciplinas");
        return ofertaDisciplinaRepository.findAll(pageable)
                .map(ofertaDisciplinaMapper::toResponseDTO);
    }

    /**
     * Busca ofertas por período
     */
    @Transactional(readOnly = true)
    public Page<OfertaDisciplinaResponseDTO> buscarPorPeriodo(String periodo, Pageable pageable) {
        log.debug("Buscando ofertas do período: {}", periodo);
        return ofertaDisciplinaRepository.findByPeriodo(periodo, pageable)
                .map(ofertaDisciplinaMapper::toResponseDTO);
    }

    /**
     * Busca ofertas por docente
     */
    @Transactional(readOnly = true)
    public Page<OfertaDisciplinaResponseDTO> buscarPorDocente(Long docenteId, Pageable pageable) {
        log.debug("Buscando ofertas do docente: {}", docenteId);

        if (!docenteRepository.existsById(docenteId)) {
            throw new ResourceNotFoundException("Docente não encontrado");
        }

        return ofertaDisciplinaRepository.findByDocenteResponsavelId(docenteId, pageable)
                .map(ofertaDisciplinaMapper::toResponseDTO);
    }

    /**
     * Busca ofertas com vagas disponíveis
     */
    @Transactional(readOnly = true)
    public List<OfertaDisciplinaResponseDTO> buscarComVagasDisponiveis() {
        log.debug("Buscando ofertas com vagas disponíveis");
        return ofertaDisciplinaRepository.findComVagasDisponiveis().stream()
                .map(ofertaDisciplinaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Abre inscrições para a oferta
     */
    @Transactional
    public OfertaDisciplinaResponseDTO abrirInscricoes(Long id) {
        log.info("Abrindo inscrições para oferta: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        if (oferta.getStatus() != StatusOferta.PLANEJADA && oferta.getStatus() != StatusOferta.FECHADA) {
            throw new BusinessException("Só é possível abrir inscrições de ofertas planejadas ou fechadas");
        }

        oferta.setStatus(StatusOferta.ABERTA);
        oferta = ofertaDisciplinaRepository.save(oferta);

        log.info("Inscrições abertas para oferta: {}", id);
        return ofertaDisciplinaMapper.toResponseDTO(oferta);
    }

    /**
     * Fecha inscrições para a oferta
     */
    @Transactional
    public OfertaDisciplinaResponseDTO fecharInscricoes(Long id) {
        log.info("Fechando inscrições para oferta: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        if (oferta.getStatus() != StatusOferta.ABERTA) {
            throw new BusinessException("Só é possível fechar inscrições de ofertas abertas");
        }

        oferta.setStatus(StatusOferta.FECHADA);
        oferta = ofertaDisciplinaRepository.save(oferta);

        log.info("Inscrições fechadas para oferta: {}", id);
        return ofertaDisciplinaMapper.toResponseDTO(oferta);
    }

    /**
     * Inicia a oferta (coloca em andamento)
     */
    @Transactional
    public OfertaDisciplinaResponseDTO iniciar(Long id) {
        log.info("Iniciando oferta: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        if (oferta.getStatus() != StatusOferta.FECHADA && oferta.getStatus() != StatusOferta.ABERTA) {
            throw new BusinessException("Só é possível iniciar ofertas fechadas ou abertas");
        }

        // Validar número mínimo de alunos
        if (oferta.getDisciplina().getMinimoAlunos() != null &&
            oferta.getVagasOcupadas() < oferta.getDisciplina().getMinimoAlunos()) {
            throw new BusinessException(
                    String.format("Número mínimo de alunos não atingido. Mínimo: %d, Matriculados: %d",
                            oferta.getDisciplina().getMinimoAlunos(), oferta.getVagasOcupadas())
            );
        }

        oferta.setStatus(StatusOferta.EM_CURSO);
        oferta = ofertaDisciplinaRepository.save(oferta);

        log.info("Oferta iniciada: {}", id);
        return ofertaDisciplinaMapper.toResponseDTO(oferta);
    }

    /**
     * Conclui a oferta
     */
    @Transactional
    public OfertaDisciplinaResponseDTO concluir(Long id) {
        log.info("Concluindo oferta: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        if (oferta.getStatus() != StatusOferta.EM_CURSO) {
            throw new BusinessException("Só é possível concluir ofertas em andamento");
        }

        // Validar se todas as matrículas têm resultado
        long matriculasSemResultado = matriculaRepository.findParaLancamentoResultado(id).size();
        if (matriculasSemResultado > 0) {
            throw new BusinessException(
                    String.format("Existem %d matrículas sem resultado final lançado", matriculasSemResultado)
            );
        }

        oferta.setStatus(StatusOferta.CONCLUIDA);
        oferta = ofertaDisciplinaRepository.save(oferta);

        log.info("Oferta concluída: {}", id);
        return ofertaDisciplinaMapper.toResponseDTO(oferta);
    }

    /**
     * Cancela a oferta
     */
    @Transactional
    public OfertaDisciplinaResponseDTO cancelar(Long id, String motivo) {
        log.info("Cancelando oferta: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        if (!oferta.podeCancelar()) {
            throw new BusinessException("Não é possível cancelar esta oferta");
        }

        oferta.setStatus(StatusOferta.CANCELADA);
        if (motivo != null && !motivo.isBlank()) {
            oferta.setObservacoes(
                    (oferta.getObservacoes() != null ? oferta.getObservacoes() + "\n" : "") +
                    "CANCELAMENTO: " + motivo
            );
        }
        oferta = ofertaDisciplinaRepository.save(oferta);

        log.info("Oferta cancelada: {}", id);
        return ofertaDisciplinaMapper.toResponseDTO(oferta);
    }

    /**
     * Deleta uma oferta (só se não tiver matrículas)
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando oferta: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        // Validar se não tem matrículas
        long matriculas = matriculaRepository.countByOfertaDisciplinaId(id);
        if (matriculas > 0) {
            throw new BusinessException("Não é possível deletar oferta com matrículas cadastradas");
        }

        ofertaDisciplinaRepository.delete(oferta);
        log.info("Oferta deletada com sucesso: {}", id);
    }

    /**
     * Obtém vagas disponíveis de uma oferta
     */
    @Transactional(readOnly = true)
    public Integer getVagasDisponiveis(Long id) {
        log.debug("Obtendo vagas disponíveis da oferta: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        return oferta.calcularVagasDisponiveis();
    }

    /**
     * Obtém estatísticas de uma oferta
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEstatisticas(Long id) {
        log.debug("Obtendo estatísticas da oferta: {}", id);

        OfertaDisciplina oferta = ofertaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta de disciplina não encontrada"));

        Object[] stats = matriculaRepository.calcularEstatisticasOferta(id);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ofertaId", id);
        resultado.put("disciplina", oferta.getDisciplina().getNome());
        resultado.put("periodo", oferta.getPeriodo());
        resultado.put("vagasOferecidas", oferta.getVagasOferecidas());
        resultado.put("vagasOcupadas", oferta.getVagasOcupadas());
        resultado.put("vagasDisponiveis", oferta.calcularVagasDisponiveis());
        resultado.put("percentualOcupacao", oferta.calcularPercentualOcupacao());

        if (stats != null && stats.length >= 6) {
            resultado.put("totalMatriculas", stats[0]);
            resultado.put("aprovados", stats[1]);
            resultado.put("reprovados", stats[2]);
            resultado.put("trancados", stats[3]);
            resultado.put("mediaGeral", stats[4]);
            resultado.put("mediaFrequencia", stats[5]);
        }

        return resultado;
    }
}
