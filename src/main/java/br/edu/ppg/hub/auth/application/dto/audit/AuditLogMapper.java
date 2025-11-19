package br.edu.ppg.hub.auth.application.dto.audit;

import br.edu.ppg.hub.auth.domain.model.AuditLog;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre AuditLog e seus DTOs.
 */
@Component
public class AuditLogMapper {

    /**
     * Converte entidade AuditLog para DTO de resposta.
     */
    public AuditLogResponseDTO toResponseDTO(AuditLog auditLog) {
        return AuditLogResponseDTO.builder()
                .id(auditLog.getId())
                .usuarioId(auditLog.getUsuario() != null ? auditLog.getUsuario().getId() : null)
                .usuarioNome(auditLog.getUsuario() != null ? auditLog.getUsuario().getNomeCompleto() : "Sistema")
                .usuarioEmail(auditLog.getUsuario() != null ? auditLog.getUsuario().getEmail() : null)
                .acao(auditLog.getAcao())
                .entidade(auditLog.getEntidade())
                .entidadeId(auditLog.getEntidadeId())
                .dadosAnteriores(auditLog.getDadosAnteriores())
                .dadosNovos(auditLog.getDadosNovos())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .createdAt(auditLog.getCreatedAt())
                .descricao(auditLog.getDescricao())
                .build();
    }
}
