package com.ppghub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA representando um Programa de Pós-Graduação.
 */
@Entity
@Table(name = "programas", indexes = {
    @Index(name = "idx_programas_instituicao", columnList = "instituicao_id"),
    @Index(name = "idx_programas_codigo_capes", columnList = "codigo_capes"),
    @Index(name = "idx_programas_area_conhecimento", columnList = "area_conhecimento"),
    @Index(name = "idx_programas_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEntity instituicao;

    @NotBlank(message = "Nome do programa é obrigatório")
    @Size(max = 255)
    @Column(nullable = false)
    private String nome;

    @Size(max = 50)
    private String sigla;

    @Size(max = 20)
    @Column(name = "codigo_capes", unique = true)
    private String codigoCapes;

    // Classificação
    @Size(max = 100)
    @Column(name = "area_conhecimento")
    private String areaConhecimento;

    @Size(max = 100)
    @Column(name = "area_avaliacao")
    private String areaAvaliacao;

    @Size(max = 50)
    private String modalidade; // ACADEMICO, PROFISSIONAL

    @Size(max = 50)
    private String nivel; // MESTRADO, DOUTORADO, MESTRADO_DOUTORADO

    // Avaliação CAPES
    @Min(value = 1, message = "Conceito CAPES deve ser entre 1 e 7")
    @Max(value = 7, message = "Conceito CAPES deve ser entre 1 e 7")
    @Column(name = "conceito_capes")
    private Integer conceitoCapes;

    @Column(name = "ano_avaliacao")
    private Integer anoAvaliacao;

    // Contato
    @Size(max = 255)
    private String coordenador;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String telefone;

    @Size(max = 500)
    private String website;

    // Status
    @Size(max = 50)
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'ATIVO'")
    private String status; // ATIVO, INATIVO, EM_IMPLANTACAO

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    // Relacionamento com docentes
    @OneToMany(mappedBy = "programa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocenteProgramaEntity> docentes = new ArrayList<>();

    /**
     * Adiciona um docente ao programa
     */
    public void addDocente(DocenteProgramaEntity docentePrograma) {
        docentes.add(docentePrograma);
        docentePrograma.setPrograma(this);
    }

    /**
     * Remove um docente do programa
     */
    public void removeDocente(DocenteProgramaEntity docentePrograma) {
        docentes.remove(docentePrograma);
        docentePrograma.setPrograma(null);
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (status == null || status.isBlank()) {
            status = "ATIVO";
        }
    }
}
