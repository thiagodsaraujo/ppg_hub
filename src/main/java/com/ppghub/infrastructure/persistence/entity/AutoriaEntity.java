package com.ppghub.infrastructure.persistence.entity;

import io.hypersistence.utils.hibernate.type.array.LongArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * Entidade JPA representando a autoria de uma publicação.
 * Relaciona publicações com docentes/autores.
 */
@Entity
@Table(name = "autorias", indexes = {
    @Index(name = "idx_autorias_publicacao", columnList = "publicacao_id"),
    @Index(name = "idx_autorias_docente", columnList = "docente_id"),
    @Index(name = "idx_autorias_position", columnList = "position_order"),
    @Index(name = "idx_autorias_raw_openalex", columnList = "raw_author_openalex_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicacao_id", nullable = false)
    @NotNull
    private PublicacaoEntity publicacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private DocenteEntity docente;

    // Informações do autor
    @Size(max = 20)
    @Column(name = "author_position")
    private String authorPosition; // first, middle, last

    @NotNull
    @Column(name = "position_order", nullable = false)
    private Integer positionOrder;

    @Column(name = "is_corresponding")
    private Boolean isCorresponding;

    // Dados brutos (caso não tenha docente associado)
    @Size(max = 255)
    @Column(name = "raw_author_name")
    private String rawAuthorName;

    @Size(max = 50)
    @Column(name = "raw_author_openalex_id")
    private String rawAuthorOpenalexId;

    @Size(max = 19)
    @Column(name = "raw_author_orcid")
    private String rawAuthorOrcid;

    // Afiliação conforme publicação
    @Column(name = "instituicao_ids", columnDefinition = "bigint[]")
    private Long[] instituicaoIds;

    @Column(name = "raw_affiliations", columnDefinition = "jsonb")
    private String rawAffiliations;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isCorresponding == null) {
            isCorresponding = false;
        }
    }
}
