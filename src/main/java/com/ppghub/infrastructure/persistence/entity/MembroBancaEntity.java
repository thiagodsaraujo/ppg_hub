package com.ppghub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade JPA representando um membro de uma banca de defesa.
 * Um membro pode ser um docente interno (DocenteEntity) ou um professor externo (ProfessorExternoEntity).
 */
@Entity
@Table(name = "membros_banca", indexes = {
    @Index(name = "idx_membros_banca_banca", columnList = "banca_id"),
    @Index(name = "idx_membros_banca_docente", columnList = "docente_id"),
    @Index(name = "idx_membros_banca_prof_ext", columnList = "professor_externo_id"),
    @Index(name = "idx_membros_banca_tipo", columnList = "tipo_membro"),
    @Index(name = "idx_membros_banca_funcao", columnList = "funcao"),
    @Index(name = "idx_membros_banca_status", columnList = "status_convite")
}, uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_membro_banca_docente",
        columnNames = {"banca_id", "docente_id"}
    ),
    @UniqueConstraint(
        name = "uk_membro_banca_prof_externo",
        columnNames = {"banca_id", "professor_externo_id"}
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembroBancaEntity extends BaseEntity {

    @NotNull(message = "Banca é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banca_id", nullable = false)
    private BancaEntity banca;

    /**
     * Docente interno participando da banca.
     * Exatamente um entre docente ou professorExterno deve ser não-nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private DocenteEntity docente;

    /**
     * Professor externo participando da banca.
     * Exatamente um entre docente ou professorExterno deve ser não-nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_externo_id")
    private ProfessorExternoEntity professorExterno;

    @NotNull(message = "Tipo de membro é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_membro", nullable = false, length = 20)
    private TipoMembro tipoMembro;

    @NotNull(message = "Função é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Funcao funcao;

    @NotNull(message = "Status do convite é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status_convite", nullable = false, length = 20)
    private StatusConvite statusConvite = StatusConvite.PENDENTE;

    @Column(name = "data_convite")
    private LocalDateTime dataConvite;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    /**
     * Indica a ordem de apresentação/participação na banca
     */
    @Column(name = "ordem_apresentacao")
    private Integer ordemApresentacao;

    /**
     * Enum para tipo de membro
     */
    public enum TipoMembro {
        TITULAR,
        SUPLENTE
    }

    /**
     * Enum para função do membro
     */
    public enum Funcao {
        PRESIDENTE,
        MEMBRO_INTERNO,
        MEMBRO_EXTERNO,
        ORIENTADOR,
        COORIENTADOR
    }

    /**
     * Enum para status do convite
     */
    public enum StatusConvite {
        PENDENTE,
        ENVIADO,
        CONFIRMADO,
        RECUSADO,
        CANCELADO
    }

    /**
     * Verifica se o membro é externo (professor externo ou docente de outra instituição)
     */
    public boolean isExterno() {
        return professorExterno != null || funcao == Funcao.MEMBRO_EXTERNO;
    }

    /**
     * Verifica se o membro é interno
     */
    public boolean isInterno() {
        return !isExterno();
    }

    /**
     * Verifica se o convite foi confirmado
     */
    public boolean isConfirmado() {
        return statusConvite == StatusConvite.CONFIRMADO;
    }

    /**
     * Verifica se o convite foi recusado
     */
    public boolean isRecusado() {
        return statusConvite == StatusConvite.RECUSADO;
    }

    /**
     * Verifica se é um membro titular
     */
    public boolean isTitular() {
        return tipoMembro == TipoMembro.TITULAR;
    }

    /**
     * Verifica se é um membro suplente
     */
    public boolean isSuplente() {
        return tipoMembro == TipoMembro.SUPLENTE;
    }

    /**
     * Obtém o nome do membro (docente ou professor externo)
     */
    public String getNomeMembro() {
        if (docente != null) {
            return docente.getNomeCompleto();
        } else if (professorExterno != null) {
            return professorExterno.getNome();
        }
        return null;
    }

    /**
     * Obtém o email do membro (docente ou professor externo)
     */
    public String getEmailMembro() {
        if (docente != null) {
            return docente.getEmail();
        } else if (professorExterno != null) {
            return professorExterno.getEmail();
        }
        return null;
    }

    /**
     * Confirma a participação do membro
     */
    public void confirmarParticipacao() {
        this.statusConvite = StatusConvite.CONFIRMADO;
        this.dataResposta = LocalDateTime.now();
    }

    /**
     * Recusa a participação do membro
     */
    public void recusarParticipacao(String motivo) {
        this.statusConvite = StatusConvite.RECUSADO;
        this.dataResposta = LocalDateTime.now();
        if (motivo != null && !motivo.isBlank()) {
            this.observacoes = (this.observacoes != null ? this.observacoes + "\n" : "") +
                             "Motivo da recusa: " + motivo;
        }
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        // Validação: exatamente um entre docente ou professorExterno deve estar definido
        boolean temDocente = docente != null;
        boolean temProfessorExterno = professorExterno != null;

        if (!temDocente && !temProfessorExterno) {
            throw new IllegalStateException(
                "MembroBancaEntity deve ter docente OU professorExterno definido"
            );
        }

        if (temDocente && temProfessorExterno) {
            throw new IllegalStateException(
                "MembroBancaEntity não pode ter docente E professorExterno ao mesmo tempo"
            );
        }

        // Se não tem status de convite, define como pendente
        if (statusConvite == null) {
            statusConvite = StatusConvite.PENDENTE;
        }

        // Se confirma ou recusa, registra data de resposta se ainda não tem
        if ((statusConvite == StatusConvite.CONFIRMADO || statusConvite == StatusConvite.RECUSADO) &&
            dataResposta == null) {
            dataResposta = LocalDateTime.now();
        }

        // Se envia convite, registra data de convite se ainda não tem
        if (statusConvite == StatusConvite.ENVIADO && dataConvite == null) {
            dataConvite = LocalDateTime.now();
        }
    }
}
