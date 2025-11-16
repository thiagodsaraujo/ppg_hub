package com.ppghub.infrastructure.persistence.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entidade JPA representando uma Publicação científica (Work no OpenAlex).
 */
@Entity
@Table(name = "publicacoes", indexes = {
    @Index(name = "idx_publicacoes_openalex_id", columnList = "openalex_work_id"),
    @Index(name = "idx_publicacoes_doi", columnList = "doi"),
    @Index(name = "idx_publicacoes_ano", columnList = "ano_publicacao"),
    @Index(name = "idx_publicacoes_tipo", columnList = "tipo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacaoEntity extends BaseEntity {

    // Identificadores
    @NotBlank(message = "OpenAlex Work ID é obrigatório")
    @Size(max = 50)
    @Column(name = "openalex_work_id", unique = true, nullable = false)
    private String openalexWorkId;

    @Size(max = 255)
    @Column(unique = true)
    private String doi;

    @Size(max = 50)
    private String pmid;

    @Size(max = 50)
    private String pmcid;

    // Dados Básicos
    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String titulo;

    @Column(name = "titulo_original", columnDefinition = "TEXT")
    private String tituloOriginal;

    @Column(name = "abstract", columnDefinition = "TEXT")
    private String resumo;

    // Publicação
    @Min(value = 1900)
    @Max(value = 2100)
    @Column(name = "ano_publicacao", nullable = false)
    private Integer anoPublicacao;

    @Column(name = "data_publicacao")
    private LocalDate dataPublicacao;

    @Size(max = 50)
    private String tipo; // article, book-chapter, dissertation, etc

    @Size(max = 10)
    private String idioma;

    // Fonte (Journal/Conference)
    @Size(max = 500)
    @Column(name = "fonte_nome")
    private String fonteNome;

    @Size(max = 20)
    @Column(name = "fonte_issn")
    private String fonteIssn;

    @Size(max = 50)
    @Column(name = "fonte_openalex_id")
    private String fonteOpenalexId;

    @Size(max = 50)
    private String volume;

    @Size(max = 50)
    private String issue;

    @Size(max = 20)
    @Column(name = "pagina_inicial")
    private String paginaInicial;

    @Size(max = 20)
    @Column(name = "pagina_final")
    private String paginaFinal;

    // Métricas
    @Column(name = "cited_by_count")
    private Integer citedByCount;

    @Column(name = "is_retracted")
    private Boolean isRetracted;

    @Column(name = "is_paratext")
    private Boolean isParatext;

    @Column(name = "is_oa")
    private Boolean isOa;

    // OpenAlex Concepts/Topics (JSONB)
    @Column(name = "concepts", columnDefinition = "jsonb")
    private String concepts;

    @Column(name = "topics", columnDefinition = "jsonb")
    private String topics;

    @Column(name = "keywords", columnDefinition = "jsonb")
    private String keywords;

    // URLs
    @Size(max = 1000)
    @Column(name = "landing_page_url")
    private String landingPageUrl;

    @Size(max = 1000)
    @Column(name = "pdf_url")
    private String pdfUrl;

    // Metadata
    @Column(name = "raw_openalex_data", columnDefinition = "jsonb")
    private String rawOpenalexData;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    // Relacionamento com autores
    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("positionOrder ASC")
    @Builder.Default
    private List<AutoriaEntity> autorias = new ArrayList<>();

    /**
     * Adiciona uma autoria à publicação
     */
    public void addAutoria(AutoriaEntity autoria) {
        autorias.add(autoria);
        autoria.setPublicacao(this);
    }

    /**
     * Remove uma autoria da publicação
     */
    public void removeAutoria(AutoriaEntity autoria) {
        autorias.remove(autoria);
        autoria.setPublicacao(null);
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (citedByCount == null) {
            citedByCount = 0;
        }
        if (isRetracted == null) {
            isRetracted = false;
        }
        if (isParatext == null) {
            isParatext = false;
        }
        if (isOa == null) {
            isOa = false;
        }
    }
}
