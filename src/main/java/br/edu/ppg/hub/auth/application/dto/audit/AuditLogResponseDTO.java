package br.edu.ppg.hub.auth.application.dto.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta com dados de um log de auditoria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private String acao;
    private String entidade;
    private Long entidadeId;
    private String dadosAnteriores;
    private String dadosNovos;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private String descricao;
}
