package br.edu.ppg.hub.academic.application.service;

import br.edu.ppg.hub.academic.application.dto.metrica_docente.MetricaDocenteCreateDTO;
import br.edu.ppg.hub.academic.application.dto.metrica_docente.MetricaDocenteMapper;
import br.edu.ppg.hub.academic.application.dto.metrica_docente.MetricaDocenteResponseDTO;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.MetricaDocente;
import br.edu.ppg.hub.academic.infrastructure.repository.DocenteRepository;
import br.edu.ppg.hub.academic.infrastructure.repository.MetricaDocenteRepository;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para operações com Métricas de Docentes
 *
 * @author PPG Hub
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricaDocenteService {

    private final MetricaDocenteRepository metricaDocenteRepository;
    private final DocenteRepository docenteRepository;
    private final MetricaDocenteMapper metricaDocenteMapper;

    /**
     * Registra uma nova métrica para um docente
     */
    @Transactional
    public MetricaDocenteResponseDTO registrarNovaMetrica(MetricaDocenteCreateDTO dto) {
        log.info("Registrando nova métrica para docente: {}", dto.getDocenteId());

        Docente docente = docenteRepository.findById(dto.getDocenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Docente não encontrado"));

        MetricaDocente metrica = metricaDocenteMapper.toEntity(dto, docente);
        metrica = metricaDocenteRepository.save(metrica);

        log.info("Métrica registrada com sucesso: {}", metrica.getId());
        return metricaDocenteMapper.toResponseDTO(metrica);
    }

    /**
     * Busca métrica por ID
     */
    @Transactional(readOnly = true)
    public MetricaDocenteResponseDTO buscarPorId(Long id) {
        log.debug("Buscando métrica: {}", id);

        MetricaDocente metrica = metricaDocenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Métrica não encontrada"));

        return metricaDocenteMapper.toResponseDTO(metrica);
    }

    /**
     * Busca todas as métricas de um docente
     */
    @Transactional(readOnly = true)
    public Page<MetricaDocenteResponseDTO> buscarPorDocente(Long docenteId, Pageable pageable) {
        log.debug("Buscando métricas do docente: {}", docenteId);

        if (!docenteRepository.existsById(docenteId)) {
            throw new ResourceNotFoundException("Docente não encontrado");
        }

        return metricaDocenteRepository.findByDocenteId(docenteId, pageable)
                .map(metricaDocenteMapper::toResponseDTO);
    }

    /**
     * Busca histórico de métricas de um docente (ordenado por data)
     */
    @Transactional(readOnly = true)
    public List<MetricaDocenteResponseDTO> getHistoricoMetricas(Long docenteId) {
        log.debug("Buscando histórico de métricas do docente: {}", docenteId);

        if (!docenteRepository.existsById(docenteId)) {
            throw new ResourceNotFoundException("Docente não encontrado");
        }

        return metricaDocenteRepository.findByDocenteIdOrderByDataColetaDesc(docenteId)
                .stream()
                .map(metricaDocenteMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca a métrica mais recente de um docente
     */
    @Transactional(readOnly = true)
    public Optional<MetricaDocenteResponseDTO> getUltimaMetrica(Long docenteId) {
        log.debug("Buscando última métrica do docente: {}", docenteId);

        if (!docenteRepository.existsById(docenteId)) {
            throw new ResourceNotFoundException("Docente não encontrado");
        }

        return metricaDocenteRepository.findUltimaMetricaByDocenteId(docenteId)
                .map(metricaDocenteMapper::toResponseDTO);
    }

    /**
     * Busca métricas de um docente por fonte
     */
    @Transactional(readOnly = true)
    public List<MetricaDocenteResponseDTO> buscarPorDocenteEFonte(Long docenteId, String fonte) {
        log.debug("Buscando métricas do docente {} da fonte {}", docenteId, fonte);

        if (!docenteRepository.existsById(docenteId)) {
            throw new ResourceNotFoundException("Docente não encontrado");
        }

        return metricaDocenteRepository.findByDocenteIdAndFonteOrderByDataColetaDesc(docenteId, fonte)
                .stream()
                .map(metricaDocenteMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca docentes com alta produtividade de um programa
     */
    @Transactional(readOnly = true)
    public List<MetricaDocenteResponseDTO> buscarDocentesAltaProdutividade(Long programaId) {
        log.debug("Buscando docentes com alta produtividade do programa: {}", programaId);

        return metricaDocenteRepository.findDocentesAltaProdutividadePorPrograma(programaId)
                .stream()
                .map(metricaDocenteMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca docentes que atendem aos critérios mínimos da CAPES
     */
    @Transactional(readOnly = true)
    public List<MetricaDocenteResponseDTO> buscarDocentesQueAtendemCapes(Long programaId) {
        log.debug("Buscando docentes que atendem CAPES do programa: {}", programaId);

        return metricaDocenteRepository.findDocentesQueAtendemCapesPorPrograma(programaId)
                .stream()
                .map(metricaDocenteMapper::toResponseDTO)
                .toList();
    }

    /**
     * Deleta uma métrica
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando métrica: {}", id);

        MetricaDocente metrica = metricaDocenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Métrica não encontrada"));

        metricaDocenteRepository.delete(metrica);

        log.info("Métrica deletada com sucesso: {}", id);
    }
}
