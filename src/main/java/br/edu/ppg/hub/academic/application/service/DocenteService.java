package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.docente.DocenteCreateDTO;
import br.edu.ppg.hub.academic.application.dto.docente.DocenteMapper;
import br.edu.ppg.hub.academic.application.dto.docente.DocenteResponseDTO;
import br.edu.ppg.hub.academic.application.dto.docente.DocenteUpdateDTO;
import br.edu.ppg.hub.academic.domain.enums.StatusDocente;
import br.edu.ppg.hub.academic.domain.enums.TipoVinculoDocente;
import br.edu.ppg.hub.academic.domain.model.Docente;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service para operações com Docentes
 *
 * @author PPG Hub
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocenteService {

    private final DocenteRepository docenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaRepository programaRepository;
    private final LinhaPesquisaRepository linhaPesquisaRepository;
    private final DocenteMapper docenteMapper;

    /**
     * Cria um novo docente
     */
    @Transactional
    public DocenteResponseDTO criar(DocenteCreateDTO dto) {
        log.info("Criando novo docente para usuário {} no programa {}", dto.getUsuarioId(), dto.getProgramaId());

        // Validar se já existe docente com mesmo usuário e programa
        if (docenteRepository.existsByUsuarioIdAndProgramaId(dto.getUsuarioId(), dto.getProgramaId())) {
            throw new DuplicateResourceException("Docente já está vinculado a este programa");
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

            // Validar se a linha de pesquisa pertence ao programa
            if (!linhaPesquisa.getPrograma().getId().equals(programa.getId())) {
                throw new BusinessException("Linha de pesquisa não pertence ao programa informado");
            }
        }

        // Criar docente
        Docente docente = docenteMapper.toEntity(dto, usuario, programa, linhaPesquisa);
        docente = docenteRepository.save(docente);

        log.info("Docente criado com sucesso: {}", docente.getId());
        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Atualiza um docente existente
     */
    @Transactional
    public DocenteResponseDTO atualizar(Long id, DocenteUpdateDTO dto) {
        log.info("Atualizando docente: {}", id);

        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        LinhaPesquisa linhaPesquisa = null;
        if (dto.getLinhaPesquisaId() != null) {
            linhaPesquisa = linhaPesquisaRepository.findById(dto.getLinhaPesquisaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada"));

            // Validar se a linha de pesquisa pertence ao programa do docente
            if (!linhaPesquisa.getPrograma().getId().equals(docente.getPrograma().getId())) {
                throw new BusinessException("Linha de pesquisa não pertence ao programa do docente");
            }
        }

        docenteMapper.updateEntity(docente, dto, linhaPesquisa);
        docente = docenteRepository.save(docente);

        log.info("Docente atualizado com sucesso: {}", id);
        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Busca docente por ID
     */
    @Transactional(readOnly = true)
    public DocenteResponseDTO buscarPorId(Long id) {
        log.debug("Buscando docente: {}", id);

        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Busca todos os docentes (paginado)
     */
    @Transactional(readOnly = true)
    public Page<DocenteResponseDTO> buscarTodos(Pageable pageable) {
        log.debug("Buscando todos os docentes");
        return docenteRepository.findAll(pageable)
                .map(docenteMapper::toResponseDTO);
    }

    /**
     * Busca docentes por programa
     */
    @Transactional(readOnly = true)
    public Page<DocenteResponseDTO> buscarPorPrograma(Long programaId, Pageable pageable) {
        log.debug("Buscando docentes do programa: {}", programaId);

        // Validar se o programa existe
        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        return docenteRepository.findByProgramaId(programaId, pageable)
                .map(docenteMapper::toResponseDTO);
    }

    /**
     * Busca docentes por programa e status
     */
    @Transactional(readOnly = true)
    public Page<DocenteResponseDTO> buscarPorProgramaEStatus(Long programaId, StatusDocente status, Pageable pageable) {
        log.debug("Buscando docentes do programa {} com status {}", programaId, status);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        return docenteRepository.findByProgramaIdAndStatus(programaId, status, pageable)
                .map(docenteMapper::toResponseDTO);
    }

    /**
     * Busca docentes por tipo de vínculo
     */
    @Transactional(readOnly = true)
    public Page<DocenteResponseDTO> buscarPorTipoVinculo(TipoVinculoDocente tipoVinculo, Pageable pageable) {
        log.debug("Buscando docentes com tipo de vínculo: {}", tipoVinculo);
        return docenteRepository.findByTipoVinculo(tipoVinculo, pageable)
                .map(docenteMapper::toResponseDTO);
    }

    /**
     * Busca docentes por linha de pesquisa
     */
    @Transactional(readOnly = true)
    public Page<DocenteResponseDTO> buscarPorLinhaPesquisa(Long linhaPesquisaId, Pageable pageable) {
        log.debug("Buscando docentes da linha de pesquisa: {}", linhaPesquisaId);

        if (!linhaPesquisaRepository.existsById(linhaPesquisaId)) {
            throw new ResourceNotFoundException("Linha de pesquisa não encontrada");
        }

        return docenteRepository.findByLinhaPesquisaId(linhaPesquisaId, pageable)
                .map(docenteMapper::toResponseDTO);
    }

    /**
     * Busca docentes com orientações disponíveis
     */
    @Transactional(readOnly = true)
    public List<DocenteResponseDTO> buscarComOrientacoesDisponiveis(Long programaId, int limiteOrientacoes) {
        log.debug("Buscando docentes com orientações disponíveis no programa: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        return docenteRepository.findDocentesComOrientacoesDisponiveis(programaId, limiteOrientacoes)
                .stream()
                .map(docenteMapper::toResponseDTO)
                .toList();
    }

    /**
     * Ativa um docente
     */
    @Transactional
    public DocenteResponseDTO ativar(Long id) {
        log.info("Ativando docente: {}", id);

        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        docente.setStatus(StatusDocente.ATIVO);
        docente.setDataDesvinculacao(null);
        docente.setMotivoDesligamento(null);

        docente = docenteRepository.save(docente);

        log.info("Docente ativado com sucesso: {}", id);
        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Desativa um docente
     */
    @Transactional
    public DocenteResponseDTO desativar(Long id, String motivo) {
        log.info("Desativando docente: {}", id);

        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        docente.setStatus(StatusDocente.DESLIGADO);
        docente.setDataDesvinculacao(LocalDate.now());
        docente.setMotivoDesligamento(motivo);

        docente = docenteRepository.save(docente);

        log.info("Docente desativado com sucesso: {}", id);
        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Afasta um docente temporariamente
     */
    @Transactional
    public DocenteResponseDTO afastar(Long id, String motivo) {
        log.info("Afastando docente: {}", id);

        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        docente.setStatus(StatusDocente.AFASTADO);
        docente.setMotivoDesligamento(motivo);

        docente = docenteRepository.save(docente);

        log.info("Docente afastado com sucesso: {}", id);
        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Aposenta um docente
     */
    @Transactional
    public DocenteResponseDTO aposentar(Long id) {
        log.info("Aposentando docente: {}", id);

        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        docente.setStatus(StatusDocente.APOSENTADO);
        docente.setDataDesvinculacao(LocalDate.now());
        docente.setMotivoDesligamento("Aposentadoria");

        docente = docenteRepository.save(docente);

        log.info("Docente aposentado com sucesso: {}", id);
        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Vincula docente a uma linha de pesquisa
     */
    @Transactional
    public DocenteResponseDTO vincularLinhaPesquisa(Long docenteId, Long linhaPesquisaId) {
        log.info("Vinculando docente {} à linha de pesquisa {}", docenteId, linhaPesquisaId);

        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        LinhaPesquisa linhaPesquisa = linhaPesquisaRepository.findById(linhaPesquisaId)
                .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada"));

        // Validar se a linha de pesquisa pertence ao programa do docente
        if (!linhaPesquisa.getPrograma().getId().equals(docente.getPrograma().getId())) {
            throw new BusinessException("Linha de pesquisa não pertence ao programa do docente");
        }

        docente.setLinhaPesquisa(linhaPesquisa);
        docente = docenteRepository.save(docente);

        log.info("Docente vinculado à linha de pesquisa com sucesso");
        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Desvincula docente de uma linha de pesquisa
     */
    @Transactional
    public DocenteResponseDTO desvincularLinhaPesquisa(Long docenteId) {
        log.info("Desvinculando docente {} da linha de pesquisa", docenteId);

        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        docente.setLinhaPesquisa(null);
        docente = docenteRepository.save(docente);

        log.info("Docente desvinculado da linha de pesquisa com sucesso");
        return docenteMapper.toResponseDTO(docente);
    }

    /**
     * Retorna estatísticas de docentes de um programa
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEstatisticasPorPrograma(Long programaId) {
        log.debug("Calculando estatísticas de docentes do programa: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", docenteRepository.countByProgramaId(programaId));
        stats.put("ativos", docenteRepository.countByProgramaIdAndStatus(programaId, StatusDocente.ATIVO));
        stats.put("afastados", docenteRepository.countByProgramaIdAndStatus(programaId, StatusDocente.AFASTADO));
        stats.put("permanentes", docenteRepository.countByProgramaIdAndTipoVinculo(programaId, TipoVinculoDocente.PERMANENTE));
        stats.put("colaboradores", docenteRepository.countByProgramaIdAndTipoVinculo(programaId, TipoVinculoDocente.COLABORADOR));
        stats.put("bolsistasProdutividade", docenteRepository.findBolsistasProdutividade(programaId).size());

        return stats;
    }

    /**
     * Deleta um docente
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando docente: {}", id);

        Docente docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        docenteRepository.delete(docente);

        log.info("Docente deletado com sucesso: {}", id);
    }
}
