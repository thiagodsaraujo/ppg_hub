package br.edu.ppg.hub.core.application.service;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.infrastructure.repository.UsuarioRepository;
import br.edu.ppg.hub.core.application.dto.linha_pesquisa.LinhaPesquisaCreateDTO;
import br.edu.ppg.hub.core.application.dto.linha_pesquisa.LinhaPesquisaMapper;
import br.edu.ppg.hub.core.application.dto.linha_pesquisa.LinhaPesquisaResponseDTO;
import br.edu.ppg.hub.core.application.dto.linha_pesquisa.LinhaPesquisaUpdateDTO;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
import br.edu.ppg.hub.core.infrastructure.repository.LinhaPesquisaRepository;
import br.edu.ppg.hub.core.infrastructure.repository.ProgramaRepository;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de Linhas de Pesquisa.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LinhaPesquisaService {

    private final LinhaPesquisaRepository linhaPesquisaRepository;
    private final ProgramaRepository programaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LinhaPesquisaMapper linhaPesquisaMapper;

    /**
     * Busca todas as linhas de pesquisa com paginação.
     */
    public Page<LinhaPesquisaResponseDTO> findAll(Pageable pageable) {
        log.debug("Buscando todas as linhas de pesquisa - página {}", pageable.getPageNumber());
        return linhaPesquisaRepository.findAll(pageable)
                .map(linhaPesquisaMapper::toResponseDTO);
    }

    /**
     * Busca linha de pesquisa por ID.
     */
    public LinhaPesquisaResponseDTO findById(Long id) {
        log.debug("Buscando linha de pesquisa por ID: {}", id);
        LinhaPesquisa linha = linhaPesquisaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada com ID: " + id));
        return linhaPesquisaMapper.toResponseDTO(linha);
    }

    /**
     * Busca linhas de pesquisa por programa.
     */
    public Page<LinhaPesquisaResponseDTO> findByPrograma(Long programaId, Pageable pageable) {
        log.debug("Buscando linhas de pesquisa do programa ID: {}", programaId);
        return linhaPesquisaRepository.findByProgramaId(programaId, pageable)
                .map(linhaPesquisaMapper::toResponseDTO);
    }

    /**
     * Busca linhas de pesquisa ativas por programa.
     */
    public List<LinhaPesquisaResponseDTO> findAtivasByPrograma(Long programaId) {
        log.debug("Buscando linhas de pesquisa ativas do programa ID: {}", programaId);
        return linhaPesquisaRepository.findByProgramaIdAndAtivaTrue(programaId).stream()
                .map(linhaPesquisaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca linhas de pesquisa por nome.
     */
    public Page<LinhaPesquisaResponseDTO> findByNome(String nome, Pageable pageable) {
        log.debug("Buscando linhas de pesquisa por nome: {}", nome);
        return linhaPesquisaRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(linhaPesquisaMapper::toResponseDTO);
    }

    /**
     * Cria nova linha de pesquisa.
     */
    @Transactional
    public LinhaPesquisaResponseDTO create(LinhaPesquisaCreateDTO dto) {
        log.info("Criando nova linha de pesquisa: {}", dto.getNome());

        // Validar programa
        Programa programa = programaRepository.findById(dto.getProgramaId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado com ID: " + dto.getProgramaId()));

        // Validar unicidade de nome no programa
        if (linhaPesquisaRepository.existsByProgramaIdAndNome(dto.getProgramaId(), dto.getNome())) {
            throw new IllegalArgumentException("Já existe uma linha de pesquisa com o nome '" + dto.getNome() + "' neste programa");
        }

        // Buscar coordenador (opcional)
        Usuario coordenador = null;
        if (dto.getCoordenadorId() != null) {
            coordenador = usuarioRepository.findById(dto.getCoordenadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador não encontrado com ID: " + dto.getCoordenadorId()));
        }

        // Criar linha de pesquisa
        LinhaPesquisa linha = linhaPesquisaMapper.toEntity(dto, programa, coordenador);
        LinhaPesquisa saved = linhaPesquisaRepository.save(linha);

        log.info("Linha de pesquisa criada com sucesso: {} (ID: {})", saved.getNome(), saved.getId());
        return linhaPesquisaMapper.toResponseDTO(saved);
    }

    /**
     * Atualiza linha de pesquisa existente.
     */
    @Transactional
    public LinhaPesquisaResponseDTO update(Long id, LinhaPesquisaUpdateDTO dto) {
        log.info("Atualizando linha de pesquisa ID: {}", id);

        LinhaPesquisa linha = linhaPesquisaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada com ID: " + id));

        // Validar nome se foi alterado
        if (dto.getNome() != null && !dto.getNome().equals(linha.getNome())) {
            if (linhaPesquisaRepository.existsByProgramaIdAndNome(linha.getPrograma().getId(), dto.getNome())) {
                throw new IllegalArgumentException("Já existe uma linha de pesquisa com o nome '" + dto.getNome() + "' neste programa");
            }
        }

        // Buscar coordenador se foi alterado
        Usuario coordenador = null;
        if (dto.getCoordenadorId() != null) {
            coordenador = usuarioRepository.findById(dto.getCoordenadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador não encontrado com ID: " + dto.getCoordenadorId()));
        }

        // Atualizar linha de pesquisa
        linhaPesquisaMapper.updateEntity(linha, dto, coordenador);
        LinhaPesquisa updated = linhaPesquisaRepository.save(linha);

        log.info("Linha de pesquisa atualizada com sucesso: {} (ID: {})", updated.getNome(), updated.getId());
        return linhaPesquisaMapper.toResponseDTO(updated);
    }

    /**
     * Deleta linha de pesquisa.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando linha de pesquisa ID: {}", id);

        if (!linhaPesquisaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Linha de pesquisa não encontrada com ID: " + id);
        }

        linhaPesquisaRepository.deleteById(id);
        log.info("Linha de pesquisa deletada com sucesso: ID {}", id);
    }

    /**
     * Ativa linha de pesquisa.
     */
    @Transactional
    public LinhaPesquisaResponseDTO ativar(Long id) {
        log.info("Ativando linha de pesquisa ID: {}", id);

        LinhaPesquisa linha = linhaPesquisaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada com ID: " + id));

        linha.ativar();
        LinhaPesquisa updated = linhaPesquisaRepository.save(linha);

        log.info("Linha de pesquisa ativada: {} (ID: {})", updated.getNome(), updated.getId());
        return linhaPesquisaMapper.toResponseDTO(updated);
    }

    /**
     * Desativa linha de pesquisa.
     */
    @Transactional
    public LinhaPesquisaResponseDTO desativar(Long id) {
        log.info("Desativando linha de pesquisa ID: {}", id);

        LinhaPesquisa linha = linhaPesquisaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Linha de pesquisa não encontrada com ID: " + id));

        linha.desativar();
        LinhaPesquisa updated = linhaPesquisaRepository.save(linha);

        log.info("Linha de pesquisa desativada: {} (ID: {})", updated.getNome(), updated.getId());
        return linhaPesquisaMapper.toResponseDTO(updated);
    }

    /**
     * Retorna estatísticas de linhas de pesquisa.
     */
    public Map<String, Object> getEstatisticas() {
        log.debug("Calculando estatísticas de linhas de pesquisa");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", linhaPesquisaRepository.count());

        return stats;
    }

    /**
     * Retorna estatísticas por programa.
     */
    public Map<String, Object> getEstatisticasPorPrograma(Long programaId) {
        log.debug("Calculando estatísticas de linhas de pesquisa do programa ID: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado com ID: " + programaId);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", linhaPesquisaRepository.countByProgramaId(programaId));
        stats.put("ativas", linhaPesquisaRepository.countByProgramaIdAndAtivaTrue(programaId));

        return stats;
    }
}
