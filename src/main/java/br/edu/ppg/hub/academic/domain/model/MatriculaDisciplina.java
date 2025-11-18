package br.edu.ppg.hub.academic.domain.model;

import br.edu.ppg.hub.academic.domain.enums.StatusMatricula;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa a Matrícula de um Discente em uma Oferta de Disciplina.
 *
 * Registra a participação do aluno na disciplina, incluindo avaliações, frequência,
 * notas e o resultado final (aprovação/reprovação).
 *
 * Segue schema: academic.matriculas_disciplinas
 *
 * @author Sistema PPG Hub
 * @since 1.0
 */
@Entity
@Table(
        name = "matriculas_disciplinas",
        schema = "academic",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_matricula_discente_oferta",
                        columnNames = {"discente_id", "oferta_disciplina_id"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Discente matriculado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discente_id", nullable = false)
    @NotNull(message = "Discente é obrigatório")
    private Discente discente;

    /**
     * Oferta de disciplina na qual o discente está matriculado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oferta_disciplina_id", nullable = false)
    @NotNull(message = "Oferta de disciplina é obrigatória")
    private OfertaDisciplina ofertaDisciplina;

    /**
     * Data e hora da matrícula
     */
    @Column(name = "data_matricula", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime dataMatricula;

    /**
     * Situação atual da matrícula: Matriculado, Trancado ou Cancelado
     */
    @NotNull(message = "Situação é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Matriculado'")
    private StatusMatricula situacao = StatusMatricula.MATRICULADO;

    /**
     * Avaliações realizadas em formato JSON
     * Exemplo: {"P1": {"tipo": "Prova", "nota": 8.5, "peso": 0.4, "data": "2024-05-15"},
     *           "T1": {"tipo": "Trabalho", "nota": 9.0, "peso": 0.6, "data": "2024-06-20"}}
     */
    @Type(JsonType.class)
    @Column(name = "avaliacoes", columnDefinition = "jsonb default '{}'")
    private String avaliacoes = "{}";

    /**
     * Percentual de frequência (0 a 100)
     */
    @DecimalMin(value = "0.0", message = "Frequência não pode ser negativa")
    @DecimalMax(value = "100.0", message = "Frequência não pode ser maior que 100")
    @Digits(integer = 3, fraction = 2, message = "Frequência deve ter formato válido")
    @Column(name = "frequencia_percentual", precision = 5, scale = 2)
    private BigDecimal frequenciaPercentual;

    /**
     * Nota final (0 a 10)
     */
    @DecimalMin(value = "0.0", message = "Nota final não pode ser negativa")
    @DecimalMax(value = "10.0", message = "Nota final não pode ser maior que 10")
    @Digits(integer = 2, fraction = 2, message = "Nota final deve ter formato válido")
    @Column(name = "nota_final", precision = 4, scale = 2)
    private BigDecimal notaFinal;

    /**
     * Conceito final (A, B, C, D, E, Aprovado, Reprovado)
     */
    @Size(max = 10, message = "Conceito deve ter no máximo 10 caracteres")
    @Column(length = 10)
    private String conceito;

    /**
     * Status final: Aprovado, Reprovado, Trancado ou Cancelado
     */
    @Size(max = 50, message = "Status final deve ter no máximo 50 caracteres")
    @Column(name = "status_final", length = 50)
    private String statusFinal;

    /**
     * Data do resultado final
     */
    @Column(name = "data_resultado")
    private LocalDate dataResultado;

    /**
     * Observações sobre a matrícula
     */
    @Column(columnDefinition = "TEXT")
    private String observacoes;

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
    // Constantes para critérios de aprovação
    // =====================================================

    private static final BigDecimal NOTA_MINIMA_APROVACAO = new BigDecimal("7.0");
    private static final BigDecimal FREQUENCIA_MINIMA_APROVACAO = new BigDecimal("75.0");

    // =====================================================
    // Métodos Helper
    // =====================================================

    /**
     * Verifica se o discente foi aprovado
     * Critérios: nota >= 7.0 E frequência >= 75%
     *
     * @return true se foi aprovado
     */
    public boolean isAprovado() {
        if (notaFinal == null || frequenciaPercentual == null) {
            return false;
        }
        return notaFinal.compareTo(NOTA_MINIMA_APROVACAO) >= 0 &&
               frequenciaPercentual.compareTo(FREQUENCIA_MINIMA_APROVACAO) >= 0;
    }

    /**
     * Verifica se o discente foi reprovado
     *
     * @return true se foi reprovado
     */
    public boolean isReprovado() {
        if (notaFinal == null || frequenciaPercentual == null) {
            return false;
        }
        return !isAprovado();
    }

    /**
     * Verifica se a matrícula está ativa
     *
     * @return true se está matriculado
     */
    public boolean isAtiva() {
        return this.situacao == StatusMatricula.MATRICULADO;
    }

    /**
     * Verifica se a matrícula foi trancada
     *
     * @return true se foi trancada
     */
    public boolean isTrancada() {
        return this.situacao == StatusMatricula.TRANCADO;
    }

    /**
     * Verifica se pode trancar a matrícula
     *
     * @return true se está matriculado
     */
    public boolean podeTranscar() {
        return this.situacao == StatusMatricula.MATRICULADO;
    }

    /**
     * Calcula a média final baseado nas avaliações
     * Este método deve ser implementado junto com a lógica de parsing do JSON de avaliações
     *
     * @return média calculada ou null se não houver avaliações
     */
    public BigDecimal calcularMediaFinal() {
        // Implementação simplificada - retorna a nota final já armazenada
        // A implementação completa deveria parsear o JSON de avaliações
        return this.notaFinal;
    }

    /**
     * Verifica se o discente atingiu a frequência mínima
     *
     * @return true se frequência >= 75%
     */
    public boolean atingiuFrequenciaMinima() {
        if (frequenciaPercentual == null) {
            return false;
        }
        return frequenciaPercentual.compareTo(FREQUENCIA_MINIMA_APROVACAO) >= 0;
    }

    /**
     * Verifica se o discente atingiu a nota mínima
     *
     * @return true se nota >= 7.0
     */
    public boolean atingiuNotaMinima() {
        if (notaFinal == null) {
            return false;
        }
        return notaFinal.compareTo(NOTA_MINIMA_APROVACAO) >= 0;
    }

    /**
     * Calcula o resultado final baseado em nota e frequência
     * Atualiza automaticamente o statusFinal
     *
     * @return status calculado (Aprovado ou Reprovado)
     */
    public String calcularResultadoFinal() {
        if (isAprovado()) {
            this.statusFinal = "Aprovado";
            this.situacao = StatusMatricula.APROVADO;
            this.conceito = calcularConceito();
        } else if (notaFinal != null && frequenciaPercentual != null) {
            this.statusFinal = "Reprovado";
            this.situacao = StatusMatricula.REPROVADO;

            if (!atingiuFrequenciaMinima()) {
                this.conceito = "Reprovado por Falta";
            } else {
                this.conceito = "Reprovado por Nota";
            }
        }
        this.dataResultado = LocalDate.now();
        return this.statusFinal;
    }

    /**
     * Calcula o conceito baseado na nota final
     * A: 9.0-10.0, B: 8.0-8.9, C: 7.0-7.9, D: 6.0-6.9, E: 0-5.9
     *
     * @return conceito calculado
     */
    private String calcularConceito() {
        if (notaFinal == null) {
            return null;
        }

        if (notaFinal.compareTo(new BigDecimal("9.0")) >= 0) {
            return "A";
        } else if (notaFinal.compareTo(new BigDecimal("8.0")) >= 0) {
            return "B";
        } else if (notaFinal.compareTo(new BigDecimal("7.0")) >= 0) {
            return "C";
        } else if (notaFinal.compareTo(new BigDecimal("6.0")) >= 0) {
            return "D";
        } else {
            return "E";
        }
    }

    /**
     * Define a nota final com arredondamento para 2 casas decimais
     *
     * @param nota nota a ser definida
     */
    public void setNotaFinalComArredondamento(BigDecimal nota) {
        if (nota != null) {
            this.notaFinal = nota.setScale(2, RoundingMode.HALF_UP);
        } else {
            this.notaFinal = null;
        }
    }

    /**
     * Define a frequência com arredondamento para 2 casas decimais
     *
     * @param frequencia frequência a ser definida
     */
    public void setFrequenciaComArredondamento(BigDecimal frequencia) {
        if (frequencia != null) {
            this.frequenciaPercentual = frequencia.setScale(2, RoundingMode.HALF_UP);
        } else {
            this.frequenciaPercentual = null;
        }
    }

    /**
     * Tranca a matrícula
     */
    public void trancar() {
        if (!podeTranscar()) {
            throw new IllegalStateException("Matrícula não pode ser trancada no estado atual");
        }
        this.situacao = StatusMatricula.TRANCADO;
        this.statusFinal = "Trancado";
        this.dataResultado = LocalDate.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (dataMatricula == null) {
            dataMatricula = LocalDateTime.now();
        }
        if (situacao == null) {
            situacao = StatusMatricula.MATRICULADO;
        }
        if (avaliacoes == null) {
            avaliacoes = "{}";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
