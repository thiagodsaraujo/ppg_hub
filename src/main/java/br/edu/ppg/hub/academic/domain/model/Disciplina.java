package br.edu.ppg.hub.academic.domain.model;

import br.edu.ppg.hub.academic.domain.enums.StatusDisciplina;
import br.edu.ppg.hub.academic.domain.enums.TipoDisciplina;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
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

import java.time.LocalDateTime;

/**
 * Entidade que representa uma Disciplina de um Programa de Pós-Graduação.
 *
 * Uma disciplina pertence a um programa e pode ser obrigatória, eletiva ou de tópicos especiais.
 * Contém toda a estrutura curricular incluindo ementa, bibliografia, carga horária e créditos.
 *
 * Segue schema: academic.disciplinas
 *
 * @author Sistema PPG Hub
 * @since 1.0
 */
@Entity
@Table(
        name = "disciplinas",
        schema = "academic",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_disciplina_codigo", columnNames = {"programa_id", "codigo"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Programa ao qual a disciplina pertence
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    @NotNull(message = "Programa é obrigatório")
    private Programa programa;

    /**
     * Código único da disciplina dentro do programa
     */
    @NotBlank(message = "Código é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String codigo;

    /**
     * Nome da disciplina em português
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String nome;

    /**
     * Nome da disciplina em inglês
     */
    @Size(max = 255, message = "Nome em inglês deve ter no máximo 255 caracteres")
    @Column(name = "nome_ingles")
    private String nomeIngles;

    /**
     * Ementa da disciplina em português
     */
    @Column(columnDefinition = "TEXT")
    private String ementa;

    /**
     * Ementa da disciplina em inglês
     */
    @Column(name = "ementa_ingles", columnDefinition = "TEXT")
    private String ementaIngles;

    /**
     * Objetivos da disciplina
     */
    @Column(columnDefinition = "TEXT")
    private String objetivos;

    /**
     * Conteúdo programático detalhado
     */
    @Column(name = "conteudo_programatico", columnDefinition = "TEXT")
    private String conteudoProgramatico;

    /**
     * Metodologia de ensino utilizada
     */
    @Column(name = "metodologia_ensino", columnDefinition = "TEXT")
    private String metodologiaEnsino;

    /**
     * Critérios de avaliação
     */
    @Column(name = "criterios_avaliacao", columnDefinition = "TEXT")
    private String criteriosAvaliacao;

    /**
     * Bibliografia básica
     */
    @Column(name = "bibliografia_basica", columnDefinition = "TEXT")
    private String bibliografiaBasica;

    /**
     * Bibliografia complementar
     */
    @Column(name = "bibliografia_complementar", columnDefinition = "TEXT")
    private String bibliografiaComplementar;

    /**
     * Carga horária total da disciplina
     */
    @NotNull(message = "Carga horária total é obrigatória")
    @Min(value = 1, message = "Carga horária total deve ser maior que zero")
    @Column(name = "carga_horaria_total", nullable = false)
    private Integer cargaHorariaTotal;

    /**
     * Carga horária teórica
     */
    @Min(value = 0, message = "Carga horária teórica não pode ser negativa")
    @Column(name = "carga_horaria_teorica", columnDefinition = "INTEGER DEFAULT 0")
    private Integer cargaHorariaTeorica = 0;

    /**
     * Carga horária prática
     */
    @Min(value = 0, message = "Carga horária prática não pode ser negativa")
    @Column(name = "carga_horaria_pratica", columnDefinition = "INTEGER DEFAULT 0")
    private Integer cargaHorariaPratica = 0;

    /**
     * Número de créditos da disciplina
     */
    @NotNull(message = "Créditos são obrigatórios")
    @Min(value = 1, message = "Créditos devem ser maior que zero")
    @Column(nullable = false)
    private Integer creditos;

    /**
     * Tipo da disciplina: Obrigatória, Eletiva, Seminário ou Tópicos Especiais
     */
    @NotNull(message = "Tipo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoDisciplina tipo;

    /**
     * Nível da disciplina: Mestrado, Doutorado ou Ambos
     */
    @NotBlank(message = "Nível é obrigatório")
    @Column(nullable = false, length = 50)
    private String nivel;

    /**
     * Linha de pesquisa associada (opcional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linha_pesquisa_id")
    private LinhaPesquisa linhaPesquisa;

    /**
     * Pré-requisitos em formato JSON (lista de IDs de disciplinas)
     */
    @Type(JsonType.class)
    @Column(name = "pre_requisitos", columnDefinition = "jsonb default '[]'")
    private String preRequisitos = "[]";

    /**
     * Co-requisitos em formato JSON (lista de IDs de disciplinas)
     */
    @Type(JsonType.class)
    @Column(name = "co_requisitos", columnDefinition = "jsonb default '[]'")
    private String coRequisitos = "[]";

    /**
     * Modalidade de ensino: Presencial, EAD ou Híbrida
     */
    @Column(length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Presencial'")
    private String modalidade = "Presencial";

    /**
     * Periodicidade de oferta: Anual, Semestral ou Eventual
     */
    @Column(length = 50)
    private String periodicidade;

    /**
     * Número máximo de alunos por turma
     */
    @Column(name = "maximo_alunos")
    private Integer maximoAlunos;

    /**
     * Número mínimo de alunos para abertura de turma
     */
    @Min(value = 1, message = "Mínimo de alunos deve ser pelo menos 1")
    @Column(name = "minimo_alunos", columnDefinition = "INTEGER DEFAULT 1")
    private Integer minimoAlunos = 1;

    /**
     * Status da disciplina: Ativa, Inativa ou Suspensa
     */
    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Ativa'")
    private StatusDisciplina status = StatusDisciplina.ATIVA;

    /**
     * Data de criação do registro
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data da última atualização do registro
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =====================================================
    // Métodos Helper
    // =====================================================

    /**
     * Verifica se a disciplina está ativa
     *
     * @return true se o status é ATIVA
     */
    public boolean isAtiva() {
        return this.status == StatusDisciplina.ATIVA;
    }

    /**
     * Calcula os créditos baseado na carga horária total
     * Regra: 15 horas = 1 crédito
     *
     * @return número de créditos calculado
     */
    public Integer calcularCreditos() {
        if (cargaHorariaTotal == null || cargaHorariaTotal == 0) {
            return 0;
        }
        return cargaHorariaTotal / 15;
    }

    /**
     * Valida se a carga horária total está correta
     *
     * @return true se a soma de teórica e prática é igual ao total
     */
    public boolean validarCargaHoraria() {
        if (cargaHorariaTotal == null) {
            return false;
        }
        int teorica = cargaHorariaTeorica != null ? cargaHorariaTeorica : 0;
        int pratica = cargaHorariaPratica != null ? cargaHorariaPratica : 0;
        return cargaHorariaTotal == (teorica + pratica);
    }

    /**
     * Verifica se a disciplina é obrigatória
     *
     * @return true se é obrigatória
     */
    public boolean isObrigatoria() {
        return this.tipo == TipoDisciplina.OBRIGATORIA;
    }

    /**
     * Verifica se a disciplina é eletiva
     *
     * @return true se é eletiva
     */
    public boolean isEletiva() {
        return this.tipo == TipoDisciplina.ELETIVA;
    }

    /**
     * Verifica se pode ser oferecida
     *
     * @return true se está ativa
     */
    public boolean podeSerOferecida() {
        return isAtiva();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusDisciplina.ATIVA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
