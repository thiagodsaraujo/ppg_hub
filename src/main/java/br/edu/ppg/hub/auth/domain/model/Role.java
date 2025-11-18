package br.edu.ppg.hub.auth.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Entidade Role (Papel/Perfil de usuário).
 *
 * Representa os papéis que usuários podem assumir no sistema.
 * Exemplos: ADMIN, COORDENADOR, DOCENTE, DISCENTE, etc.
 *
 * Tabela: auth.roles
 */
@Entity
@Table(name = "roles", schema = "auth")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /**
     * ID único da role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome único da role.
     * Exemplos: ROLE_ADMIN, ROLE_COORDENADOR, ROLE_DOCENTE
     *
     * IMPORTANTE: Spring Security espera que roles comecem com "ROLE_"
     */
    @NotBlank(message = "Nome da role é obrigatório")
    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    /**
     * Descrição detalhada da role.
     */
    @Column(columnDefinition = "TEXT")
    private String descricao;

    /**
     * Nível de acesso da role (1 a 5).
     * 1 = menor privilégio (ex: VISITANTE, DISCENTE)
     * 5 = maior privilégio (ex: SUPERADMIN)
     */
    @Min(value = 1, message = "Nível de acesso mínimo é 1")
    @Max(value = 5, message = "Nível de acesso máximo é 5")
    @Column(name = "nivel_acesso", nullable = false)
    private Integer nivelAcesso;

    /**
     * Permissões específicas em formato JSONB.
     *
     * Exemplo:
     * {
     *   "can_create_users": true,
     *   "can_edit_programs": true,
     *   "can_view_reports": true
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissoes", columnDefinition = "jsonb")
    private String permissoes;

    /**
     * Indica se a role está ativa.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    /**
     * Data de criação da role.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Verifica se a role tem nível administrativo (>= 3).
     */
    @Transient
    public boolean isAdministrativa() {
        return nivelAcesso != null && nivelAcesso >= 3;
    }

    /**
     * Verifica se a role tem nível superior ou igual ao especificado.
     */
    @Transient
    public boolean temNivelMinimo(int nivelMinimo) {
        return nivelAcesso != null && nivelAcesso >= nivelMinimo;
    }
}
