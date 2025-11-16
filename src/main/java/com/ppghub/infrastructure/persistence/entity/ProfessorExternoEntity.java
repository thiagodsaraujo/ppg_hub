package com.ppghub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA representando um Professor Externo (não cadastrado como Docente).
 * Usado principalmente para composição de bancas com membros de outras instituições.
 */
@Entity
@Table(name = "professores_externos", indexes = {
    @Index(name = "idx_prof_ext_nome", columnList = "nome"),
    @Index(name = "idx_prof_ext_email", columnList = "email", unique = true),
    @Index(name = "idx_prof_ext_instituicao", columnList = "instituicao_origem"),
    @Index(name = "idx_prof_ext_orcid", columnList = "orcid", unique = true),
    @Index(name = "idx_prof_ext_lattes", columnList = "lattes_id", unique = true),
    @Index(name = "idx_prof_ext_validado", columnList = "validado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessorExternoEntity extends BaseEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 255)
    @Column(name = "instituicao_origem")
    private String instituicaoOrigem;

    @Size(max = 100)
    @Column(length = 100)
    private String titulacao; // DR, MS, PhD, POS-DOC, etc

    @Size(max = 255)
    @Column(length = 255)
    private String especialidade;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    // Identificadores Acadêmicos
    @Size(max = 50)
    @Column(name = "lattes_id", unique = true, length = 50)
    private String lattesId;

    @Size(max = 19)
    @Column(unique = true, length = 19)
    private String orcid;

    @Size(max = 50)
    @Column(name = "openalex_author_id", unique = true, length = 50)
    private String openalexAuthorId;

    @Size(max = 50)
    @Column(name = "scopus_id", length = 50)
    private String scopusId;

    // OpenAlex Metrics (quando disponível)
    @Column(name = "openalex_works_count")
    private Integer openalexWorksCount;

    @Column(name = "openalex_cited_by_count")
    private Integer openalexCitedByCount;

    @Column(name = "openalex_h_index")
    private Integer openalexHIndex;

    @Column(name = "openalex_last_sync_at")
    private LocalDateTime openalexLastSyncAt;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    /**
     * Indica se os dados do professor foram validados/enriquecidos
     * via fontes externas (OpenAlex, ORCID, Lattes)
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean validado = false;

    /**
     * Indica se o professor está ativo para participar de bancas
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativo = true;

    // Relacionamento com membros de banca
    @OneToMany(mappedBy = "professorExterno", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MembroBancaEntity> participacoesBancas = new ArrayList<>();

    /**
     * Adiciona uma participação em banca
     */
    public void addParticipacaoBanca(MembroBancaEntity membro) {
        participacoesBancas.add(membro);
        membro.setProfessorExterno(this);
    }

    /**
     * Remove uma participação em banca
     */
    public void removeParticipacaoBanca(MembroBancaEntity membro) {
        participacoesBancas.remove(membro);
        membro.setProfessorExterno(null);
    }

    /**
     * Verifica se o professor tem identificadores acadêmicos
     */
    public boolean temIdentificadorAcademico() {
        return lattesId != null || orcid != null || openalexAuthorId != null;
    }

    /**
     * Verifica se o professor foi validado
     */
    public boolean isValidado() {
        return Boolean.TRUE.equals(validado);
    }

    /**
     * Marca o professor como validado
     */
    public void marcarComoValidado() {
        this.validado = true;
    }

    /**
     * Conta o número de bancas em que participou
     */
    public int getNumeroParticipacoes() {
        return participacoesBancas != null ? participacoesBancas.size() : 0;
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (validado == null) {
            validado = false;
        }
        if (ativo == null) {
            ativo = true;
        }
        if (openalexWorksCount == null) {
            openalexWorksCount = 0;
        }
        if (openalexCitedByCount == null) {
            openalexCitedByCount = 0;
        }
        if (openalexHIndex == null) {
            openalexHIndex = 0;
        }
    }
}
