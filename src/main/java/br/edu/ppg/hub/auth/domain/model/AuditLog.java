package br.edu.ppg.hub.auth.domain.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidade que representa um log de auditoria do sistema.
 *
 * Registra todas as ações importantes realizadas pelos usuários,
 * incluindo operações CRUD em entidades críticas.
 *
 * Usado para rastreabilidade, segurança e compliance.
 *
 * Segue schema: auth.audit_logs
 */
@Entity
@Table(
        name = "audit_logs",
        schema = "auth",
        indexes = {
                @Index(name = "idx_audit_usuario", columnList = "usuario_id"),
                @Index(name = "idx_audit_acao", columnList = "acao"),
                @Index(name = "idx_audit_entidade", columnList = "entidade, entidade_id"),
                @Index(name = "idx_audit_created_at", columnList = "created_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuário que executou a ação.
     * Pode ser null para ações do sistema ou usuários anônimos.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /**
     * Ação executada (ex: CREATE, UPDATE, DELETE, LOGIN, LOGOUT).
     */
    @NotBlank(message = "Ação é obrigatória")
    @Size(max = 100, message = "Ação deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String acao;

    /**
     * Nome da entidade afetada (ex: Usuario, Programa, Discente).
     */
    @Size(max = 100, message = "Entidade deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String entidade;

    /**
     * ID da entidade afetada.
     */
    @Column(name = "entidade_id")
    private Long entidadeId;

    /**
     * Dados anteriores da entidade (antes da modificação) em formato JSON.
     */
    @Type(JsonType.class)
    @Column(name = "dados_anteriores", columnDefinition = "jsonb")
    private String dadosAnteriores;

    /**
     * Dados novos da entidade (após a modificação) em formato JSON.
     */
    @Type(JsonType.class)
    @Column(name = "dados_novos", columnDefinition = "jsonb")
    private String dadosNovos;

    /**
     * Endereço IP de origem da requisição.
     */
    @Column(name = "ip_address", columnDefinition = "INET")
    private String ipAddress;

    /**
     * User agent do navegador/cliente.
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Data e hora da ação.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Retorna uma descrição legível da ação.
     */
    public String getDescricao() {
        StringBuilder sb = new StringBuilder();

        if (usuario != null) {
            sb.append(usuario.getNomeCompleto());
        } else {
            sb.append("Sistema");
        }

        sb.append(" executou ").append(acao);

        if (entidade != null) {
            sb.append(" em ").append(entidade);
            if (entidadeId != null) {
                sb.append(" #").append(entidadeId);
            }
        }

        return sb.toString();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
