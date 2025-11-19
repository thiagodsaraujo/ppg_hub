package br.edu.ppg.hub.core.application.service;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.infrastructure.repository.UsuarioRepository;
import br.edu.ppg.hub.core.application.dto.programa.ProgramaCreateDTO;
import br.edu.ppg.hub.core.application.dto.programa.ProgramaMapper;
import br.edu.ppg.hub.core.application.dto.programa.ProgramaResponseDTO;
import br.edu.ppg.hub.core.application.dto.programa.ProgramaUpdateDTO;
import br.edu.ppg.hub.core.domain.enums.StatusPrograma;
import br.edu.ppg.hub.core.domain.model.Instituicao;
import br.edu.ppg.hub.core.domain.model.Programa;
import br.edu.ppg.hub.core.infrastructure.repository.InstituicaoRepository;
import br.edu.ppg.hub.core.infrastructure.repository.ProgramaRepository;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service para gerenciamento de Programas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramaService {

    private final ProgramaRepository programaRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaMapper programaMapper;

    /**
     * Busca todos os programas com paginação.
     */
    public Page<ProgramaResponseDTO> findAll(Pageable pageable) {
        log.debug("Buscando todos os programas - página {}", pageable.getPageNumber());
        return programaRepository.findAll(pageable)
                .map(programaMapper::toResponseDTO);
    }

    /**
     * Busca programa por ID.
     */
    public ProgramaResponseDTO findById(Long id) {
        log.debug("Buscando programa por ID: {}", id);
        Programa programa = programaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado com ID: " + id));
        return programaMapper.toResponseDTO(programa);
    }

    /**
     * Busca programa por código CAPES.
     */
    public ProgramaResponseDTO findByCodigoCapes(String codigoCapes) {
        log.debug("Buscando programa por código CAPES: {}", codigoCapes);
        Programa programa = programaRepository.findByCodigoCapes(codigoCapes)
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado com código CAPES: " + codigoCapes));
        return programaMapper.toResponseDTO(programa);
    }

    /**
     * Busca programas por instituição.
     */
    public Page<ProgramaResponseDTO> findByInstituicao(Long instituicaoId, Pageable pageable) {
        log.debug("Buscando programas da instituição ID: {}", instituicaoId);
        return programaRepository.findByInstituicaoId(instituicaoId, pageable)
                .map(programaMapper::toResponseDTO);
    }

    /**
     * Busca programas por status.
     */
    public Page<ProgramaResponseDTO> findByStatus(StatusPrograma status, Pageable pageable) {
        log.debug("Buscando programas com status: {}", status);
        return programaRepository.findByStatus(status, pageable)
                .map(programaMapper::toResponseDTO);
    }

    /**
     * Busca programas por nome (busca parcial).
     */
    public Page<ProgramaResponseDTO> findByNome(String nome, Pageable pageable) {
        log.debug("Buscando programas por nome: {}", nome);
        return programaRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(programaMapper::toResponseDTO);
    }

    /**
     * Cria novo programa.
     */
    @Transactional
    public ProgramaResponseDTO create(ProgramaCreateDTO dto) {
        log.info("Criando novo programa: {}", dto.getNome());

        // Validar instituição
        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição não encontrada com ID: " + dto.getInstituicaoId()));

        // Validar unicidade de sigla na instituição
        if (programaRepository.existsByInstituicaoIdAndSigla(dto.getInstituicaoId(), dto.getSigla())) {
            throw new IllegalArgumentException("Já existe um programa com a sigla '" + dto.getSigla() + "' nesta instituição");
        }

        // Validar unicidade de código CAPES
        if (dto.getCodigoCapes() != null && programaRepository.existsByCodigoCapes(dto.getCodigoCapes())) {
            throw new IllegalArgumentException("Já existe um programa com código CAPES: " + dto.getCodigoCapes());
        }

        // Buscar coordenadores (opcional)
        Usuario coordenador = null;
        if (dto.getCoordenadorId() != null) {
            coordenador = usuarioRepository.findById(dto.getCoordenadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador não encontrado com ID: " + dto.getCoordenadorId()));
        }

        Usuario coordenadorAdjunto = null;
        if (dto.getCoordenadorAdjuntoId() != null) {
            coordenadorAdjunto = usuarioRepository.findById(dto.getCoordenadorAdjuntoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador adjunto não encontrado com ID: " + dto.getCoordenadorAdjuntoId()));
        }

        // Criar programa
        Programa programa = programaMapper.toEntity(dto, instituicao, coordenador, coordenadorAdjunto);
        Programa saved = programaRepository.save(programa);

        log.info("Programa criado com sucesso: {} (ID: {})", saved.getNome(), saved.getId());
        return programaMapper.toResponseDTO(saved);
    }

    /**
     * Atualiza programa existente.
     */
    @Transactional
    public ProgramaResponseDTO update(Long id, ProgramaUpdateDTO dto) {
        log.info("Atualizando programa ID: {}", id);

        Programa programa = programaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado com ID: " + id));

        // Validar sigla se foi alterada
        if (dto.getSigla() != null && !dto.getSigla().equals(programa.getSigla())) {
            if (programaRepository.existsByInstituicaoIdAndSigla(programa.getInstituicao().getId(), dto.getSigla())) {
                throw new IllegalArgumentException("Já existe um programa com a sigla '" + dto.getSigla() + "' nesta instituição");
            }
        }

        // Validar código CAPES se foi alterado
        if (dto.getCodigoCapes() != null && !dto.getCodigoCapes().equals(programa.getCodigoCapes())) {
            if (programaRepository.existsByCodigoCapes(dto.getCodigoCapes())) {
                throw new IllegalArgumentException("Já existe um programa com código CAPES: " + dto.getCodigoCapes());
            }
        }

        // Buscar coordenadores se foram alterados
        Usuario coordenador = null;
        if (dto.getCoordenadorId() != null) {
            coordenador = usuarioRepository.findById(dto.getCoordenadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador não encontrado com ID: " + dto.getCoordenadorId()));
        }

        Usuario coordenadorAdjunto = null;
        if (dto.getCoordenadorAdjuntoId() != null) {
            coordenadorAdjunto = usuarioRepository.findById(dto.getCoordenadorAdjuntoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coordenador adjunto não encontrado com ID: " + dto.getCoordenadorAdjuntoId()));
        }

        // Atualizar programa
        programaMapper.updateEntity(programa, dto, coordenador, coordenadorAdjunto);
        Programa updated = programaRepository.save(programa);

        log.info("Programa atualizado com sucesso: {} (ID: {})", updated.getNome(), updated.getId());
        return programaMapper.toResponseDTO(updated);
    }

    /**
     * Deleta programa.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando programa ID: {}", id);

        if (!programaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Programa não encontrado com ID: " + id);
        }

        programaRepository.deleteById(id);
        log.info("Programa deletado com sucesso: ID {}", id);
    }

    /**
     * Ativa programa.
     */
    @Transactional
    public ProgramaResponseDTO ativar(Long id) {
        log.info("Ativando programa ID: {}", id);

        Programa programa = programaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado com ID: " + id));

        programa.setStatus(StatusPrograma.ATIVO);
        Programa updated = programaRepository.save(programa);

        log.info("Programa ativado: {} (ID: {})", updated.getNome(), updated.getId());
        return programaMapper.toResponseDTO(updated);
    }

    /**
     * Suspende programa.
     */
    @Transactional
    public ProgramaResponseDTO suspender(Long id) {
        log.info("Suspendendo programa ID: {}", id);

        Programa programa = programaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado com ID: " + id));

        programa.setStatus(StatusPrograma.SUSPENSO);
        Programa updated = programaRepository.save(programa);

        log.info("Programa suspenso: {} (ID: {})", updated.getNome(), updated.getId());
        return programaMapper.toResponseDTO(updated);
    }

    /**
     * Retorna estatísticas gerais dos programas.
     */
    public Map<String, Object> getEstatisticas() {
        log.debug("Calculando estatísticas de programas");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", programaRepository.count());
        stats.put("ativos", programaRepository.findByStatus(StatusPrograma.ATIVO, Pageable.unpaged()).getTotalElements());
        stats.put("suspensos", programaRepository.findByStatus(StatusPrograma.SUSPENSO, Pageable.unpaged()).getTotalElements());
        stats.put("descredenciados", programaRepository.findByStatus(StatusPrograma.DESCREDENCIADO, Pageable.unpaged()).getTotalElements());

        return stats;
    }

    /**
     * Retorna estatísticas de uma instituição específica.
     */
    public Map<String, Object> getEstatisticasPorInstituicao(Long instituicaoId) {
        log.debug("Calculando estatísticas de programas da instituição ID: {}", instituicaoId);

        if (!instituicaoRepository.existsById(instituicaoId)) {
            throw new ResourceNotFoundException("Instituição não encontrada com ID: " + instituicaoId);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", programaRepository.countByInstituicaoId(instituicaoId));
        stats.put("ativos", programaRepository.countByInstituicaoIdAndStatus(instituicaoId, StatusPrograma.ATIVO));

        return stats;
    }
}
