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
 * Entidade JPA representando uma Instituição de Ensino Superior.
 */
@Entity
@Table(name = "instituicoes", indexes = {
    @Index(name = "idx_instituicoes_nome", columnList = "nome"),
    @Index(name = "idx_instituicoes_sigla", columnList = "sigla"),
    @Index(name = "idx_instituicoes_cidade_estado", columnList = "cidade, estado"),
    @Index(name = "idx_instituicoes_openalex_id", columnList = "openalex_institution_id"),
    @Index(name = "idx_instituicoes_ror_id", columnList = "ror_id"),
    @Index(name = "idx_instituicoes_ativo", columnList = "ativo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstituicaoEntity extends BaseEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Sigla é obrigatória")
    @Size(max = 50)
    @Column(nullable = false)
    private String sigla;

    @NotBlank(message = "Tipo é obrigatório")
    @Size(max = 50)
    @Column(nullable = false)
    private String tipo; // PUBLICA, PRIVADA, ESPECIAL

    @Size(max = 50)
    private String categoria; // FEDERAL, ESTADUAL, MUNICIPAL, PARTICULAR, COMUNITARIA

    @Size(max = 18)
    @Column(unique = true)
    private String cnpj;

    // Endereço
    @Size(max = 255)
    private String logradouro;

    @Size(max = 20)
    private String numero;

    @Size(max = 100)
    private String complemento;

    @Size(max = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100)
    @Column(nullable = false)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2)
    @Column(nullable = false, length = 2)
    private String estado;

    @Size(max = 9)
    private String cep;

    @Size(max = 100)
    @Column(columnDefinition = "VARCHAR(100) DEFAULT 'Brasil'")
    private String pais;

    // Contato
    @Size(max = 20)
    private String telefone;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 500)
    private String website;

    // OpenAlex Integration
    @Size(max = 50)
    @Column(name = "openalex_institution_id", unique = true)
    private String openalexInstitutionId;

    @Size(max = 50)
    @Column(name = "ror_id", unique = true)
    private String rorId;

    @Size(max = 255)
    @Column(name = "openalex_display_name")
    private String openalexDisplayName;

    @Column(name = "openalex_last_sync_at")
    private LocalDateTime openalexLastSyncAt;

    @Column(name = "openalex_works_count")
    private Integer openalexWorksCount;

    @Column(name = "openalex_cited_by_count")
    private Integer openalexCitedByCount;

    // Status
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativo = true;

    // Relacionamentos
    @OneToMany(mappedBy = "instituicao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProgramaEntity> programas = new ArrayList<>();

    @OneToMany(mappedBy = "instituicao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocenteEntity> docentes = new ArrayList<>();

    /**
     * Adiciona um programa à instituição
     */
    public void addPrograma(ProgramaEntity programa) {
        programas.add(programa);
        programa.setInstituicao(this);
    }

    /**
     * Remove um programa da instituição
     */
    public void removePrograma(ProgramaEntity programa) {
        programas.remove(programa);
        programa.setInstituicao(null);
    }

    /**
     * Adiciona um docente à instituição
     */
    public void addDocente(DocenteEntity docente) {
        docentes.add(docente);
        docente.setInstituicao(this);
    }

    /**
     * Remove um docente da instituição
     */
    public void removeDocente(DocenteEntity docente) {
        docentes.remove(docente);
        docente.setInstituicao(null);
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (pais == null || pais.isBlank()) {
            pais = "Brasil";
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
    }
}
