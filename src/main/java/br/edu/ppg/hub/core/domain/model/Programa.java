package br.edu.ppg.hub.core.domain.model;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.core.domain.enums.ModalidadePrograma;
import br.edu.ppg.hub.core.domain.enums.NivelPrograma;
import br.edu.ppg.hub.core.domain.enums.StatusPrograma;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Programa de Pós-Graduação.
 *
 * Um programa está vinculado a uma instituição e pode oferecer
 * mestrado, doutorado ou ambos (mestrado/doutorado).
 *
 * Segue schema: core.programas
 */
@Entity
@Table(
        name = "programas",
        schema = "core",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"instituicao_id", "sigla"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Instituição à qual o programa pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id", nullable = false)
    @NotNull(message = "Instituição é obrigatória")
    private Instituicao instituicao;

    /**
     * Código oficial da CAPES (único).
     */
    @Column(name = "codigo_capes", unique = true, length = 20)
    private String codigoCapes;

    /**
     * Nome completo do programa.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String nome;

    /**
     * Sigla do programa (ex: PPGCC, PPGD, etc.).
     */
    @NotBlank(message = "Sigla é obrigatória")
    @Size(max = 20, message = "Sigla deve ter no máximo 20 caracteres")
    @Column(nullable = false, length = 20)
    private String sigla;

    /**
     * Área de concentração principal do programa.
     */
    @Size(max = 255, message = "Área de concentração deve ter no máximo 255 caracteres")
    @Column(name = "area_concentracao")
    private String areaConcentracao;

    /**
     * Nível do programa: Mestrado, Doutorado ou Mestrado/Doutorado.
     */
    @NotNull(message = "Nível é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NivelPrograma nivel;

    /**
     * Modalidade: Presencial, EAD ou Semipresencial.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Presencial'")
    private ModalidadePrograma modalidade = ModalidadePrograma.PRESENCIAL;

    /**
     * Data de início de funcionamento do programa.
     */
    @Column(name = "inicio_funcionamento")
    private LocalDate inicioFuncionamento;

    /**
     * Conceito CAPES (nota de 1 a 7).
     */
    @Min(value = 1, message = "Conceito CAPES deve ser no mínimo 1")
    @Max(value = 7, message = "Conceito CAPES deve ser no máximo 7")
    @Column(name = "conceito_capes")
    private Integer conceitoCapes;

    /**
     * Data da última avaliação CAPES.
     */
    @Column(name = "data_ultima_avaliacao")
    private LocalDate dataUltimaAvaliacao;

    /**
     * Triênio da avaliação (ex: 2021-2024).
     */
    @Size(max = 20, message = "Triênio deve ter no máximo 20 caracteres")
    @Column(name = "trienio_avaliacao", length = 20)
    private String trienioAvaliacao;

    /**
     * Coordenador atual do programa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordenador_id")
    private Usuario coordenador;

    /**
     * Coordenador adjunto do programa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordenador_adjunto_id")
    private Usuario coordenadorAdjunto;

    /**
     * Data de início do mandato da coordenação.
     */
    @Column(name = "mandato_inicio")
    private LocalDate mandatoInicio;

    /**
     * Data de fim do mandato da coordenação.
     */
    @Column(name = "mandato_fim")
    private LocalDate mandatoFim;

    /**
     * Créditos mínimos exigidos para mestrado.
     */
    @Column(name = "creditos_minimos_mestrado", columnDefinition = "INTEGER DEFAULT 24")
    private Integer creditosMinimosMestrado = 24;

    /**
     * Créditos mínimos exigidos para doutorado.
     */
    @Column(name = "creditos_minimos_doutorado", columnDefinition = "INTEGER DEFAULT 48")
    private Integer creditosMinimosDoutorado = 48;

    /**
     * Prazo máximo para conclusão do mestrado (em meses).
     */
    @Column(name = "prazo_maximo_mestrado", columnDefinition = "INTEGER DEFAULT 24")
    private Integer prazoMaximoMestrado = 24;

    /**
     * Prazo máximo para conclusão do doutorado (em meses).
     */
    @Column(name = "prazo_maximo_doutorado", columnDefinition = "INTEGER DEFAULT 48")
    private Integer prazoMaximoDoutorado = 48;

    /**
     * ID da instituição no OpenAlex (para integração).
     */
    @Column(name = "openalex_institution_id")
    private String openalexInstitutionId;

    /**
     * Status do programa: Ativo, Suspenso ou Descredenciado.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Ativo'")
    private StatusPrograma status = StatusPrograma.ATIVO;

    /**
     * Configurações adicionais em formato JSON.
     */
    @Type(JsonType.class)
    @Column(name = "configuracoes", columnDefinition = "jsonb default '{}'")
    private String configuracoes = "{}";

    /**
     * Data de criação do registro.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data da última atualização do registro.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Verifica se o programa está ativo.
     */
    public boolean isAtivo() {
        return this.status == StatusPrograma.ATIVO;
    }

    /**
     * Verifica se o programa oferece mestrado.
     */
    public boolean ofereceMestrado() {
        return this.nivel == NivelPrograma.MESTRADO || this.nivel == NivelPrograma.MESTRADO_DOUTORADO;
    }

    /**
     * Verifica se o programa oferece doutorado.
     */
    public boolean ofereceDoutorado() {
        return this.nivel == NivelPrograma.DOUTORADO || this.nivel == NivelPrograma.MESTRADO_DOUTORADO;
    }

    /**
     * Verifica se o mandato da coordenação está vigente.
     */
    public boolean mandatoVigente() {
        if (mandatoInicio == null || mandatoFim == null) {
            return false;
        }
        LocalDate hoje = LocalDate.now();
        return !hoje.isBefore(mandatoInicio) && !hoje.isAfter(mandatoFim);
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
