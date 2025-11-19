package br.edu.ppg.hub.auth.application.service;

import br.edu.ppg.hub.auth.application.dto.audit.AuditLogMapper;
import br.edu.ppg.hub.auth.application.dto.audit.AuditLogResponseDTO;
import br.edu.ppg.hub.auth.domain.model.AuditLog;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.infrastructure.repository.AuditLogRepository;
import br.edu.ppg.hub.auth.infrastructure.security.SecurityUtils;
import br.edu.ppg.hub.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service para gerenciamento de Logs de Auditoria.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * Busca todos os logs com paginação.
     */
    public Page<AuditLogResponseDTO> findAll(Pageable pageable) {
        log.debug("Buscando todos os logs de auditoria - página {}", pageable.getPageNumber());
        return auditLogRepository.findAll(pageable)
                .map(auditLogMapper::toResponseDTO);
    }

    /**
     * Busca log por ID.
     */
    public AuditLogResponseDTO findById(Long id) {
        log.debug("Buscando log de auditoria por ID: {}", id);
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log de auditoria não encontrado com ID: " + id));
        return auditLogMapper.toResponseDTO(auditLog);
    }

    /**
     * Busca logs por usuário.
     */
    public Page<AuditLogResponseDTO> findByUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Buscando logs do usuário ID: {}", usuarioId);
        return auditLogRepository.findByUsuarioId(usuarioId, pageable)
                .map(auditLogMapper::toResponseDTO);
    }

    /**
     * Busca logs por ação.
     */
    public Page<AuditLogResponseDTO> findByAcao(String acao, Pageable pageable) {
        log.debug("Buscando logs da ação: {}", acao);
        return auditLogRepository.findByAcao(acao, pageable)
                .map(auditLogMapper::toResponseDTO);
    }

    /**
     * Busca logs por entidade.
     */
    public Page<AuditLogResponseDTO> findByEntidade(String entidade, Pageable pageable) {
        log.debug("Buscando logs da entidade: {}", entidade);
        return auditLogRepository.findByEntidade(entidade, pageable)
                .map(auditLogMapper::toResponseDTO);
    }

    /**
     * Busca logs por entidade e ID.
     */
    public Page<AuditLogResponseDTO> findByEntidadeAndId(String entidade, Long entidadeId, Pageable pageable) {
        log.debug("Buscando logs da entidade {} com ID: {}", entidade, entidadeId);
        return auditLogRepository.findByEntidadeAndEntidadeId(entidade, entidadeId, pageable)
                .map(auditLogMapper::toResponseDTO);
    }

    /**
     * Busca logs por período.
     */
    public Page<AuditLogResponseDTO> findByPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        log.debug("Buscando logs entre {} e {}", inicio, fim);
        return auditLogRepository.findByPeriodo(inicio, fim, pageable)
                .map(auditLogMapper::toResponseDTO);
    }

    /**
     * Busca logs recentes de um usuário.
     */
    public Page<AuditLogResponseDTO> findRecentByUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Buscando logs recentes do usuário ID: {}", usuarioId);
        return auditLogRepository.findRecentByUsuario(usuarioId, pageable)
                .map(auditLogMapper::toResponseDTO);
    }

    /**
     * Registra ação no log de auditoria.
     */
    @Transactional
    public void registrarAcao(String acao, String entidade, Long entidadeId, Object dadosAnteriores, Object dadosNovos) {
        try {
            Usuario usuario = SecurityUtils.getCurrentUser();
            HttpServletRequest request = getCurrentRequest();

            AuditLog auditLog = AuditLog.builder()
                    .usuario(usuario)
                    .acao(acao)
                    .entidade(entidade)
                    .entidadeId(entidadeId)
                    .dadosAnteriores(dadosAnteriores != null ? objectMapper.writeValueAsString(dadosAnteriores) : null)
                    .dadosNovos(dadosNovos != null ? objectMapper.writeValueAsString(dadosNovos) : null)
                    .ipAddress(request != null ? getClientIp(request) : null)
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Ação auditada: {} em {} #{}", acao, entidade, entidadeId);

        } catch (JsonProcessingException e) {
            log.error("Erro ao converter dados para JSON na auditoria", e);
        } catch (Exception e) {
            log.error("Erro ao registrar log de auditoria", e);
        }
    }

    /**
     * Registra criação de entidade.
     */
    public void registrarCriacao(String entidade, Long entidadeId, Object dados) {
        registrarAcao("CREATE", entidade, entidadeId, null, dados);
    }

    /**
     * Registra atualização de entidade.
     */
    public void registrarAtualizacao(String entidade, Long entidadeId, Object dadosAnteriores, Object dadosNovos) {
        registrarAcao("UPDATE", entidade, entidadeId, dadosAnteriores, dadosNovos);
    }

    /**
     * Registra exclusão de entidade.
     */
    public void registrarExclusao(String entidade, Long entidadeId, Object dados) {
        registrarAcao("DELETE", entidade, entidadeId, dados, null);
    }

    /**
     * Registra login de usuário.
     */
    public void registrarLogin(Long usuarioId) {
        registrarAcao("LOGIN", "Usuario", usuarioId, null, null);
    }

    /**
     * Registra logout de usuário.
     */
    public void registrarLogout(Long usuarioId) {
        registrarAcao("LOGOUT", "Usuario", usuarioId, null, null);
    }

    /**
     * Retorna estatísticas de logs.
     */
    public Map<String, Object> getEstatisticas() {
        log.debug("Calculando estatísticas de auditoria");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", auditLogRepository.count());
        stats.put("logins", auditLogRepository.countByAcao("LOGIN"));
        stats.put("creates", auditLogRepository.countByAcao("CREATE"));
        stats.put("updates", auditLogRepository.countByAcao("UPDATE"));
        stats.put("deletes", auditLogRepository.countByAcao("DELETE"));

        return stats;
    }

    /**
     * Limpa logs antigos (para manutenção).
     */
    @Transactional
    public void limparLogsAntigos(int diasRetencao) {
        log.info("Limpando logs de auditoria com mais de {} dias", diasRetencao);
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(diasRetencao);
        auditLogRepository.deleteByCreatedAtBefore(dataLimite);
        log.info("Logs antigos removidos com sucesso");
    }

    /**
     * Obtém requisição HTTP atual.
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obtém IP do cliente considerando proxies.
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Se houver múltiplos IPs (proxy chain), pega o primeiro
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
