package com.ppghub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Entidade JPA representando o relacionamento Many-to-Many entre Docente e Programa.
 */
@Entity
@Table(name = "docente_programa", indexes = {
    @Index(name = "idx_dp_programa", columnList = "programa_id")
})
@IdClass(DocenteProgramaEntity.DocenteProgramaId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocenteProgramaEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id", nullable = false)
    private DocenteEntity docente;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    private ProgramaEntity programa;

    @NotBlank(message = "Categoria é obrigatória")
    @Size(max = 50)
    @Column(nullable = false)
    private String categoria; // PERMANENTE, COLABORADOR, VISITANTE

    @NotNull(message = "Data de início é obrigatória")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    /**
     * Classe de ID composto para a entidade DocentePrograma
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocenteProgramaId implements Serializable {
        private Long docente;
        private Long programa;
    }
}
