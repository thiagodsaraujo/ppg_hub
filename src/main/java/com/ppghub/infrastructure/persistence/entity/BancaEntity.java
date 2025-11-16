package com.ppghub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA representando uma Banca de Defesa ou Qualificação.
 */
@Entity
@Table(name = "bancas", indexes = {
    @Index(name = "idx_bancas_discente", columnList = "discente_id"),
    @Index(name = "idx_bancas_programa", columnList = "programa_id"),
    @Index(name = "idx_bancas_tipo", columnList = "tipo_banca"),
    @Index(name = "idx_bancas_status", columnList = "status_banca"),
    @Index(name = "idx_bancas_data", columnList = "data_hora"),
    @Index(name = "idx_bancas_resultado", columnList = "resultado_banca")
}, uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_banca_discente_tipo_data",
        columnNames = {"discente_id", "tipo_banca", "data_hora"}
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BancaEntity extends BaseEntity {

    @NotNull(message = "Discente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discente_id", nullable = false)
    private DiscenteEntity discente;

    @NotNull(message = "Programa é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    private ProgramaEntity programa;

    @NotNull(message = "Tipo de banca é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_banca", nullable = false, length = 30)
    private TipoBanca tipoBanca;

    @NotNull(message = "Data e hora são obrigatórias")
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Size(max = 255)
    @Column(name = "local_defesa")
    private String localDefesa;

    @Size(max = 500)
    @Column(name = "titulo_trabalho", length = 500)
    private String tituloTrabalho;

    @NotNull(message = "Status da banca é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status_banca", nullable = false, length = 20)
    private StatusBanca statusBanca = StatusBanca.AGENDADA;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado_banca", length = 30)
    private ResultadoBanca resultadoBanca;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Size(max = 500)
    @Column(name = "documento_ata", length = 500)
    private String documentoAta;

    @Size(max = 500)
    @Column(name = "documento_tese", length = 500)
    private String documentoTese;

    @Column(name = "data_realizacao")
    private LocalDateTime dataRealizacao;

    /**
     * Indica se o orientador do discente participa da banca
     */
    @Column(name = "orientador_participa", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean orientadorParticipa = true;

    /**
     * Indica se a defesa é remota/virtual
     */
    @Column(name = "defesa_remota", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean defensaRemota = false;

    @Size(max = 500)
    @Column(name = "link_videoconferencia", length = 500)
    private String linkVideoconferencia;

    // Relacionamento com membros da banca
    @OneToMany(mappedBy = "banca", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MembroBancaEntity> membros = new ArrayList<>();

    /**
     * Enum para tipos de banca
     */
    public enum TipoBanca {
        QUALIFICACAO_MESTRADO,
        QUALIFICACAO_DOUTORADO,
        DEFESA_MESTRADO,
        DEFESA_DOUTORADO,
        DEFESA_DOUTORADO_DIRETO,
        EXAME_PROFICIENCIA
    }

    /**
     * Enum para status da banca
     */
    public enum StatusBanca {
        AGENDADA,
        CONFIRMADA,
        REALIZADA,
        CANCELADA,
        REAGENDADA
    }

    /**
     * Enum para resultado da banca
     */
    public enum ResultadoBanca {
        APROVADO,
        APROVADO_COM_RESTRICOES,
        APROVADO_COM_CORRECOES,
        REPROVADO
    }

    /**
     * Adiciona um membro à banca
     */
    public void addMembro(MembroBancaEntity membro) {
        membros.add(membro);
        membro.setBanca(this);
    }

    /**
     * Remove um membro da banca
     */
    public void removeMembro(MembroBancaEntity membro) {
        membros.remove(membro);
        membro.setBanca(null);
    }

    /**
     * Conta o número de membros titulares
     */
    public long getNumeroMembrosTitulares() {
        return membros.stream()
            .filter(m -> m.getTipoMembro() == MembroBancaEntity.TipoMembro.TITULAR)
            .count();
    }

    /**
     * Conta o número de membros externos
     */
    public long getNumeroMembrosExternos() {
        return membros.stream()
            .filter(MembroBancaEntity::isExterno)
            .count();
    }

    /**
     * Verifica se a banca já foi realizada
     */
    public boolean isRealizada() {
        return statusBanca == StatusBanca.REALIZADA;
    }

    /**
     * Verifica se a banca foi aprovada
     */
    public boolean isAprovada() {
        return resultadoBanca != null &&
               (resultadoBanca == ResultadoBanca.APROVADO ||
                resultadoBanca == ResultadoBanca.APROVADO_COM_RESTRICOES ||
                resultadoBanca == ResultadoBanca.APROVADO_COM_CORRECOES);
    }

    /**
     * Verifica se a banca pode ser cancelada
     */
    public boolean podeCancelar() {
        return statusBanca == StatusBanca.AGENDADA || statusBanca == StatusBanca.CONFIRMADA;
    }

    /**
     * Verifica se a banca pode ser reagendada
     */
    public boolean podeReagendar() {
        return statusBanca == StatusBanca.AGENDADA || statusBanca == StatusBanca.CONFIRMADA;
    }

    /**
     * Marca a banca como realizada
     */
    public void marcarComoRealizada(ResultadoBanca resultado) {
        this.statusBanca = StatusBanca.REALIZADA;
        this.resultadoBanca = resultado;
        this.dataRealizacao = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (statusBanca == null) {
            statusBanca = StatusBanca.AGENDADA;
        }
        if (orientadorParticipa == null) {
            orientadorParticipa = true;
        }
        if (defensaRemota == null) {
            defensaRemota = false;
        }

        // Validação: se realizada, deve ter resultado
        if (statusBanca == StatusBanca.REALIZADA && resultadoBanca == null) {
            throw new IllegalStateException("Banca realizada deve ter resultado definido");
        }

        // Validação: se não realizada, não deve ter resultado
        if (statusBanca != StatusBanca.REALIZADA && resultadoBanca != null) {
            resultadoBanca = null;
        }
    }
}
