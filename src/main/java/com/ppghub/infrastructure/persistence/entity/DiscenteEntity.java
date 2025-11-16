package com.ppghub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA representando um Discente (Estudante de Pós-Graduação).
 */
@Entity
@Table(name = "discentes", indexes = {
    @Index(name = "idx_discentes_matricula", columnList = "matricula", unique = true),
    @Index(name = "idx_discentes_programa", columnList = "programa_id"),
    @Index(name = "idx_discentes_orientador", columnList = "orientador_id"),
    @Index(name = "idx_discentes_nome", columnList = "nome"),
    @Index(name = "idx_discentes_email", columnList = "email", unique = true),
    @Index(name = "idx_discentes_status", columnList = "status_matricula"),
    @Index(name = "idx_discentes_cpf", columnList = "cpf", unique = true),
    @Index(name = "idx_discentes_lattes", columnList = "lattes_id", unique = true),
    @Index(name = "idx_discentes_orcid", columnList = "orcid", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscenteEntity extends BaseEntity {

    @NotBlank(message = "Matrícula é obrigatória")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String matricula;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 14)
    @Column(unique = true, length = 14)
    private String cpf;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_ingresso")
    private LocalDate dataIngresso;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_matricula", nullable = false, length = 20)
    private StatusMatricula statusMatricula = StatusMatricula.ATIVO;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_formacao", nullable = false, length = 20)
    private NivelFormacao nivelFormacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    private ProgramaEntity programa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orientador_id")
    private DocenteEntity orientador;

    @Size(max = 500)
    @Column(name = "titulo_tese", length = 500)
    private String tituloTese;

    @Column(name = "data_defesa")
    private LocalDate dataDefesa;

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

    // OpenAlex Metrics (para discentes com produção científica)
    @Column(name = "openalex_works_count")
    private Integer openalexWorksCount;

    @Column(name = "openalex_cited_by_count")
    private Integer openalexCitedByCount;

    @Column(name = "openalex_last_sync_at")
    private LocalDateTime openalexLastSyncAt;

    // Relacionamento com bancas
    @OneToMany(mappedBy = "discente", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BancaEntity> bancas = new ArrayList<>();

    /**
     * Enum para status de matrícula do discente
     */
    public enum StatusMatricula {
        ATIVO,
        INATIVO,
        EGRESSO,
        TRANCADO,
        DESLIGADO
    }

    /**
     * Enum para nível de formação
     */
    public enum NivelFormacao {
        MESTRADO,
        DOUTORADO,
        DOUTORADO_DIRETO
    }

    /**
     * Adiciona uma banca ao discente
     */
    public void addBanca(BancaEntity banca) {
        bancas.add(banca);
        banca.setDiscente(this);
    }

    /**
     * Remove uma banca do discente
     */
    public void removeBanca(BancaEntity banca) {
        bancas.remove(banca);
        banca.setDiscente(null);
    }

    /**
     * Verifica se o discente está ativo
     */
    public boolean isAtivo() {
        return statusMatricula == StatusMatricula.ATIVO;
    }

    /**
     * Verifica se o discente já defendeu
     */
    public boolean jaDefendeu() {
        return dataDefesa != null;
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (statusMatricula == null) {
            statusMatricula = StatusMatricula.ATIVO;
        }
        if (openalexWorksCount == null) {
            openalexWorksCount = 0;
        }
        if (openalexCitedByCount == null) {
            openalexCitedByCount = 0;
        }
    }
}
