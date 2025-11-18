package br.edu.ppg.hub.auth.application.service;

import br.edu.ppg.hub.auth.application.dto.vinculacao.UsuarioProgramaRoleCreateDTO;
import br.edu.ppg.hub.auth.application.dto.vinculacao.UsuarioProgramaRoleMapper;
import br.edu.ppg.hub.auth.application.dto.vinculacao.UsuarioProgramaRoleResponseDTO;
import br.edu.ppg.hub.auth.application.dto.vinculacao.UsuarioProgramaRoleUpdateDTO;
import br.edu.ppg.hub.auth.domain.enums.StatusVinculacao;
import br.edu.ppg.hub.auth.domain.model.Role;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.domain.model.UsuarioProgramaRole;
import br.edu.ppg.hub.auth.infrastructure.repository.RoleRepository;
import br.edu.ppg.hub.auth.infrastructure.repository.UsuarioProgramaRoleRepository;
import br.edu.ppg.hub.auth.infrastructure.repository.UsuarioRepository;
import br.edu.ppg.hub.auth.infrastructure.security.SecurityUtils;
import br.edu.ppg.hub.core.domain.model.Programa;
import br.edu.ppg.hub.core.infrastructure.repository.ProgramaRepository;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de Vinculações de Usuários a Programas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioProgramaRoleService {

    private final UsuarioProgramaRoleRepository vinculacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaRepository programaRepository;
    private final RoleRepository roleRepository;
    private final UsuarioProgramaRoleMapper vinculacaoMapper;

    /**
     * Busca todas as vinculações com paginação.
     */
    public Page<UsuarioProgramaRoleResponseDTO> findAll(Pageable pageable) {
        log.debug("Buscando todas as vinculações - página {}", pageable.getPageNumber());
        return vinculacaoRepository.findAll(pageable)
                .map(vinculacaoMapper::toResponseDTO);
    }

    /**
     * Busca vinculação por ID.
     */
    public UsuarioProgramaRoleResponseDTO findById(Long id) {
        log.debug("Buscando vinculação por ID: {}", id);
        UsuarioProgramaRole vinculacao = vinculacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculação não encontrada com ID: " + id));
        return vinculacaoMapper.toResponseDTO(vinculacao);
    }

    /**
     * Busca vinculações por usuário.
     */
    public Page<UsuarioProgramaRoleResponseDTO> findByUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Buscando vinculações do usuário ID: {}", usuarioId);
        return vinculacaoRepository.findByUsuarioId(usuarioId, pageable)
                .map(vinculacaoMapper::toResponseDTO);
    }

    /**
     * Busca vinculações por programa.
     */
    public Page<UsuarioProgramaRoleResponseDTO> findByPrograma(Long programaId, Pageable pageable) {
        log.debug("Buscando vinculações do programa ID: {}", programaId);
        return vinculacaoRepository.findByProgramaId(programaId, pageable)
                .map(vinculacaoMapper::toResponseDTO);
    }

    /**
     * Busca vinculações vigentes de um usuário em um programa.
     */
    public List<UsuarioProgramaRoleResponseDTO> findVinculacoesVigentes(Long usuarioId, Long programaId) {
        log.debug("Buscando vinculações vigentes do usuário {} no programa {}", usuarioId, programaId);
        return vinculacaoRepository.findVinculacoesVigentesByUsuarioAndPrograma(usuarioId, programaId).stream()
                .map(vinculacaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica se usuário tem role específica em programa.
     */
    public boolean usuarioTemRole(Long usuarioId, Long programaId, String nomeRole) {
        log.debug("Verificando se usuário {} tem role {} no programa {}", usuarioId, nomeRole, programaId);
        return vinculacaoRepository.usuarioTemRoleNoPrograma(usuarioId, programaId, nomeRole);
    }

    /**
     * Cria nova vinculação.
     */
    @Transactional
    public UsuarioProgramaRoleResponseDTO create(UsuarioProgramaRoleCreateDTO dto) {
        log.info("Criando nova vinculação: usuário {} -> programa {} com role {}",
                dto.getUsuarioId(), dto.getProgramaId(), dto.getRoleId());

        // Validar usuário
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + dto.getUsuarioId()));

        // Validar programa
        Programa programa = programaRepository.findById(dto.getProgramaId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa não encontrado com ID: " + dto.getProgramaId()));

        // Validar role
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada com ID: " + dto.getRoleId()));

        // Validar se já existe vinculação ativa
        if (vinculacaoRepository.existsByUsuarioIdAndProgramaIdAndRoleIdAndStatus(
                dto.getUsuarioId(), dto.getProgramaId(), dto.getRoleId(), StatusVinculacao.ATIVO)) {
            throw new IllegalArgumentException("Já existe uma vinculação ativa deste usuário com esta role neste programa");
        }

        // Obter usuário logado (quem está criando a vinculação)
        Usuario createdBy = SecurityUtils.getCurrentUser();

        // Criar vinculação
        UsuarioProgramaRole vinculacao = vinculacaoMapper.toEntity(dto, usuario, programa, role, createdBy);
        UsuarioProgramaRole saved = vinculacaoRepository.save(vinculacao);

        log.info("Vinculação criada com sucesso: ID {}", saved.getId());
        return vinculacaoMapper.toResponseDTO(saved);
    }

    /**
     * Atualiza vinculação existente.
     */
    @Transactional
    public UsuarioProgramaRoleResponseDTO update(Long id, UsuarioProgramaRoleUpdateDTO dto) {
        log.info("Atualizando vinculação ID: {}", id);

        UsuarioProgramaRole vinculacao = vinculacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculação não encontrada com ID: " + id));

        // Atualizar vinculação
        vinculacaoMapper.updateEntity(vinculacao, dto);
        UsuarioProgramaRole updated = vinculacaoRepository.save(vinculacao);

        log.info("Vinculação atualizada com sucesso: ID {}", updated.getId());
        return vinculacaoMapper.toResponseDTO(updated);
    }

    /**
     * Deleta vinculação.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando vinculação ID: {}", id);

        if (!vinculacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vinculação não encontrada com ID: " + id);
        }

        vinculacaoRepository.deleteById(id);
        log.info("Vinculação deletada com sucesso: ID {}", id);
    }

    /**
     * Suspende vinculação.
     */
    @Transactional
    public UsuarioProgramaRoleResponseDTO suspender(Long id) {
        log.info("Suspendendo vinculação ID: {}", id);

        UsuarioProgramaRole vinculacao = vinculacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculação não encontrada com ID: " + id));

        vinculacao.suspender();
        UsuarioProgramaRole updated = vinculacaoRepository.save(vinculacao);

        log.info("Vinculação suspensa: ID {}", updated.getId());
        return vinculacaoMapper.toResponseDTO(updated);
    }

    /**
     * Reativa vinculação.
     */
    @Transactional
    public UsuarioProgramaRoleResponseDTO reativar(Long id) {
        log.info("Reativando vinculação ID: {}", id);

        UsuarioProgramaRole vinculacao = vinculacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculação não encontrada com ID: " + id));

        vinculacao.reativar();
        UsuarioProgramaRole updated = vinculacaoRepository.save(vinculacao);

        log.info("Vinculação reativada: ID {}", updated.getId());
        return vinculacaoMapper.toResponseDTO(updated);
    }

    /**
     * Desliga usuário do programa.
     */
    @Transactional
    public UsuarioProgramaRoleResponseDTO desligar(Long id, LocalDate dataDesligamento) {
        log.info("Desligando vinculação ID: {} com data {}", id, dataDesligamento);

        UsuarioProgramaRole vinculacao = vinculacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculação não encontrada com ID: " + id));

        vinculacao.desligar(dataDesligamento);
        UsuarioProgramaRole updated = vinculacaoRepository.save(vinculacao);

        log.info("Vinculação desligada: ID {}", updated.getId());
        return vinculacaoMapper.toResponseDTO(updated);
    }

    /**
     * Retorna estatísticas de vinculações.
     */
    public Map<String, Object> getEstatisticas() {
        log.debug("Calculando estatísticas de vinculações");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", vinculacaoRepository.count());

        return stats;
    }

    /**
     * Retorna estatísticas por programa.
     */
    public Map<String, Object> getEstatisticasPorPrograma(Long programaId) {
        log.debug("Calculando estatísticas de vinculações do programa ID: {}", programaId);

        if (!programaRepository.existsById(programaId)) {
            throw new ResourceNotFoundException("Programa não encontrado com ID: " + programaId);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", vinculacaoRepository.countByProgramaId(programaId));
        stats.put("ativas", vinculacaoRepository.countByProgramaIdAndStatus(programaId, StatusVinculacao.ATIVO));

        return stats;
    }
}
