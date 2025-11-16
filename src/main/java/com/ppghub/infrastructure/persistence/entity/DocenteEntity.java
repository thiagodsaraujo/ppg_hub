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
 * Entidade JPA representando um Docente/Pesquisador.
 */
@Entity
@Table(name = "docentes", indexes = {
    @Index(name = "idx_docentes_instituicao", columnList = "instituicao_id"),
    @Index(name = "idx_docentes_nome", columnList = "nome_completo"),
    @Index(name = "idx_docentes_lattes", columnList = "lattes_id"),
    @Index(name = "idx_docentes_orcid", columnList = "orcid"),
    @Index(name = "idx_docentes_openalex", columnList = "openalex_author_id"),
    @Index(name = "idx_docentes_ativo", columnList = "ativo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocenteEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEntity instituicao;

    // Dados Pessoais
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 255)
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Size(max = 255)
    @Column(name = "nome_citacao")
    private String nomeCitacao;

    @Size(max = 14)
    @Column(unique = true)
    private String cpf;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String telefone;

    // Identificadores Acadêmicos
    @Size(max = 50)
    @Column(name = "lattes_id", unique = true)
    private String lattesId;

    @Size(max = 19)
    @Column(unique = true)
    private String orcid;

    @Size(max = 50)
    @Column(name = "openalex_author_id", unique = true)
    private String openalexAuthorId;

    @Size(max = 50)
    @Column(name = "scopus_id")
    private String scopusId;

    @Size(max = 50)
    @Column(name = "researcher_id")
    private String researcherId;

    // Formação
    @Size(max = 50)
    private String titulacao; // GRADUACAO, ESPECIALIZACAO, MESTRADO, DOUTORADO, POS_DOUTORADO

    @Size(max = 255)
    @Column(name = "area_atuacao")
    private String areaAtuacao;

    // Vínculo
    @Size(max = 50)
    @Column(name = "tipo_vinculo")
    private String tipoVinculo; // PERMANENTE, COLABORADOR, VISITANTE

    @Size(max = 50)
    @Column(name = "regime_trabalho")
    private String regimeTrabalho; // DEDICACAO_EXCLUSIVA, TEMPO_INTEGRAL, TEMPO_PARCIAL

    @Column(name = "data_ingresso")
    private LocalDate dataIngresso;

    @Column(name = "data_saida")
    private LocalDate dataSaida;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativo = true;

    // OpenAlex Metrics
    @Column(name = "openalex_works_count")
    private Integer openalexWorksCount;

    @Column(name = "openalex_cited_by_count")
    private Integer openalexCitedByCount;

    @Column(name = "openalex_h_index")
    private Integer openalexHIndex;

    @Column(name = "openalex_last_sync_at")
    private LocalDateTime openalexLastSyncAt;

    // Relacionamento com programas
    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocenteProgramaEntity> programas = new ArrayList<>();

    // Relacionamento com autorias (publicações)
    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL)
    @Builder.Default
    private List<AutoriaEntity> autorias = new ArrayList<>();

    /**
     * Adiciona um vínculo com programa
     */
    public void addPrograma(DocenteProgramaEntity docentePrograma) {
        programas.add(docentePrograma);
        docentePrograma.setDocente(this);
    }

    /**
     * Remove um vínculo com programa
     */
    public void removePrograma(DocenteProgramaEntity docentePrograma) {
        programas.remove(docentePrograma);
        docentePrograma.setDocente(null);
    }

    @PrePersist
    @PreUpdate
    public void validate() {
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
