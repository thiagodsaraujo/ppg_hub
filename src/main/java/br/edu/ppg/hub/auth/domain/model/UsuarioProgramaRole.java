package br.edu.ppg.hub.auth.domain.model;

import br.edu.ppg.hub.auth.domain.enums.StatusVinculacao;
import br.edu.ppg.hub.core.domain.model.Programa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa a vinculação de um usuário a um programa com um papel específico.
 *
 * Define o relacionamento multi-tenant: um usuário pode ter diferentes papéis
 * em diferentes programas.
 *
 * Exemplo: Um mesmo usuário pode ser COORDENADOR no Programa A e DOCENTE no Programa B.
 *
 * Segue schema: auth.usuario_programa_roles
 */
@Entity
@Table(
        name = "usuario_programa_roles",
        schema = "auth",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"usuario_id", "programa_id", "role_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioProgramaRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuário vinculado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    private Usuario usuario;

    /**
     * Programa ao qual o usuário está vinculado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    @NotNull(message = "Programa é obrigatório")
    private Programa programa;

    /**
     * Papel (role) do usuário no programa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @NotNull(message = "Role é obrigatória")
    private Role role;

    /**
     * Data de vinculação ao programa.
     */
    @Column(name = "data_vinculacao", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate dataVinculacao;

    /**
     * Data de desvinculação (null se ainda vinculado).
     */
    @Column(name = "data_desvinculacao")
    private LocalDate dataDesvinculacao;

    /**
     * Status da vinculação: Ativo, Suspenso ou Desligado.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Ativo'")
    private StatusVinculacao status = StatusVinculacao.ATIVO;

    /**
     * Observações sobre a vinculação.
     */
    @Column(columnDefinition = "TEXT")
    private String observacoes;

    /**
     * Data de criação do registro.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Usuário que criou o registro.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Usuario createdBy;

    /**
     * Verifica se a vinculação está ativa.
     */
    public boolean isAtiva() {
        return this.status == StatusVinculacao.ATIVO && this.dataDesvinculacao == null;
    }

    /**
     * Verifica se a vinculação está vigente (considerando datas).
     */
    public boolean isVigente() {
        LocalDate hoje = LocalDate.now();
        boolean dentroPeriodo = !hoje.isBefore(dataVinculacao) &&
                                (dataDesvinculacao == null || !hoje.isAfter(dataDesvinculacao));
        return isAtiva() && dentroPeriodo;
    }

    /**
     * Suspende a vinculação.
     */
    public void suspender() {
        this.status = StatusVinculacao.SUSPENSO;
    }

    /**
     * Reativa a vinculação.
     */
    public void reativar() {
        this.status = StatusVinculacao.ATIVO;
    }

    /**
     * Desliga o usuário do programa.
     */
    public void desligar(LocalDate dataDesligamento) {
        this.status = StatusVinculacao.DESLIGADO;
        this.dataDesvinculacao = dataDesligamento != null ? dataDesligamento : LocalDate.now();
    }

    @PrePersist
    protected void onCreate() {
        if (dataVinculacao == null) {
            dataVinculacao = LocalDate.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
