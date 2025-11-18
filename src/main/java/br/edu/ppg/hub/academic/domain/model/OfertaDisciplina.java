package br.edu.ppg.hub.academic.domain.model;

import br.edu.ppg.hub.academic.domain.enums.StatusOferta;
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
 * Entidade que representa uma Oferta de Disciplina em um período letivo.
 *
 * Uma oferta é a instância concreta de uma disciplina sendo ministrada em um
 * determinado período, com professor responsável, horários, vagas e sala definidos.
 *
 * Segue schema: academic.ofertas_disciplinas
 *
 * @author Sistema PPG Hub
 * @since 1.0
 */
@Entity
@Table(
        name = "ofertas_disciplinas",
        schema = "academic",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_oferta_disciplina",
                        columnNames = {"disciplina_id", "ano", "semestre", "turma"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfertaDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Disciplina sendo ofertada
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    @NotNull(message = "Disciplina é obrigatória")
    private Disciplina disciplina;

    /**
     * Docente responsável pela disciplina
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_responsavel_id", nullable = false)
    @NotNull(message = "Docente responsável é obrigatório")
    private Docente docenteResponsavel;

    /**
     * Docente colaborador (opcional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_colaborador_id")
    private Docente docenteColaborador;

    /**
     * Ano da oferta
     */
    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2020, message = "Ano deve ser maior ou igual a 2020")
    @Max(value = 2100, message = "Ano deve ser menor ou igual a 2100")
    @Column(nullable = false)
    private Integer ano;

    /**
     * Semestre da oferta (1 ou 2)
     */
    @NotNull(message = "Semestre é obrigatório")
    @Min(value = 1, message = "Semestre deve ser 1 ou 2")
    @Max(value = 2, message = "Semestre deve ser 1 ou 2")
    @Column(nullable = false)
    private Integer semestre;

    /**
     * Período no formato AAAA.S (ex: 2024.1)
     */
    @NotBlank(message = "Período é obrigatório")
    @Size(max = 10, message = "Período deve ter no máximo 10 caracteres")
    @Column(nullable = false, length = 10)
    private String periodo;

    /**
     * Identificação da turma (ex: A, B, C)
     */
    @Size(max = 10, message = "Turma deve ter no máximo 10 caracteres")
    @Column(length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'A'")
    private String turma = "A";

    /**
     * Horários das aulas em formato JSON
     * Exemplo: [{"dia": "Segunda", "inicio": "14:00", "fim": "16:00"}]
     */
    @Type(JsonType.class)
    @Column(name = "horarios", nullable = false, columnDefinition = "jsonb")
    @NotNull(message = "Horários são obrigatórios")
    private String horarios;

    /**
     * Sala ou local das aulas
     */
    @Size(max = 50, message = "Sala deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String sala;

    /**
     * Modalidade de ensino: Presencial, EAD ou Híbrida
     */
    @Column(length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Presencial'")
    private String modalidade = "Presencial";

    /**
     * Link para aulas virtuais (quando aplicável)
     */
    @Column(name = "link_virtual", columnDefinition = "TEXT")
    private String linkVirtual;

    /**
     * Data de início da oferta
     */
    @NotNull(message = "Data de início é obrigatória")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    /**
     * Data de término da oferta
     */
    @NotNull(message = "Data de fim é obrigatória")
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    /**
     * Total de vagas oferecidas
     */
    @NotNull(message = "Vagas oferecidas são obrigatórias")
    @Min(value = 1, message = "Deve haver pelo menos 1 vaga")
    @Column(name = "vagas_oferecidas", nullable = false)
    private Integer vagasOferecidas;

    /**
     * Número de vagas já ocupadas
     */
    @Min(value = 0, message = "Vagas ocupadas não podem ser negativas")
    @Column(name = "vagas_ocupadas", columnDefinition = "INTEGER DEFAULT 0")
    private Integer vagasOcupadas = 0;

    /**
     * Número de alunos em lista de espera
     */
    @Min(value = 0, message = "Lista de espera não pode ser negativa")
    @Column(name = "lista_espera", columnDefinition = "INTEGER DEFAULT 0")
    private Integer listaEspera = 0;

    /**
     * Status da oferta: Planejada, Aberta, Fechada, Em_Curso, Concluída ou Cancelada
     */
    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'Planejada'")
    private StatusOferta status = StatusOferta.PLANEJADA;

    /**
     * Observações gerais sobre a oferta
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
    // Métodos Helper
    // =====================================================

    /**
     * Verifica se há vagas disponíveis
     *
     * @return true se há vagas disponíveis
     */
    public boolean temVagasDisponiveis() {
        if (vagasOferecidas == null || vagasOcupadas == null) {
            return false;
        }
        return vagasOcupadas < vagasOferecidas;
    }

    /**
     * Calcula o número de vagas disponíveis
     *
     * @return número de vagas disponíveis
     */
    public Integer calcularVagasDisponiveis() {
        if (vagasOferecidas == null || vagasOcupadas == null) {
            return 0;
        }
        return Math.max(0, vagasOferecidas - vagasOcupadas);
    }

    /**
     * Verifica se as inscrições estão abertas
     *
     * @return true se o status permite inscrições
     */
    public boolean isInscricoesAbertas() {
        return this.status == StatusOferta.ABERTA && temVagasDisponiveis();
    }

    /**
     * Verifica se a oferta está ativa
     *
     * @return true se está em curso
     */
    public boolean isAtiva() {
        return this.status == StatusOferta.EM_CURSO;
    }

    /**
     * Verifica se a oferta foi concluída
     *
     * @return true se foi concluída
     */
    public boolean isConcluida() {
        return this.status == StatusOferta.CONCLUIDA;
    }

    /**
     * Verifica se a oferta pode ser cancelada
     *
     * @return true se pode ser cancelada
     */
    public boolean podeCancelar() {
        return this.status != StatusOferta.CONCLUIDA &&
               this.status != StatusOferta.CANCELADA;
    }

    /**
     * Incrementa o número de vagas ocupadas
     *
     * @throws IllegalStateException se não houver vagas disponíveis
     */
    public void incrementarVagasOcupadas() {
        if (!temVagasDisponiveis()) {
            throw new IllegalStateException("Não há vagas disponíveis");
        }
        this.vagasOcupadas++;
    }

    /**
     * Decrementa o número de vagas ocupadas
     *
     * @throws IllegalStateException se já não houver vagas ocupadas
     */
    public void decrementarVagasOcupadas() {
        if (this.vagasOcupadas == null || this.vagasOcupadas <= 0) {
            throw new IllegalStateException("Não há vagas ocupadas para decrementar");
        }
        this.vagasOcupadas--;
    }

    /**
     * Calcula o percentual de ocupação
     *
     * @return percentual de 0 a 100
     */
    public Double calcularPercentualOcupacao() {
        if (vagasOferecidas == null || vagasOferecidas == 0) {
            return 0.0;
        }
        int ocupadas = vagasOcupadas != null ? vagasOcupadas : 0;
        return (ocupadas * 100.0) / vagasOferecidas;
    }

    /**
     * Verifica se permite lançamento de notas
     *
     * @return true se está em curso ou concluída
     */
    public boolean permiteLancarNotas() {
        return this.status == StatusOferta.EM_CURSO || this.status == StatusOferta.CONCLUIDA;
    }

    /**
     * Valida as datas da oferta
     *
     * @return true se data fim é posterior à data início
     */
    public boolean validarDatas() {
        if (dataInicio == null || dataFim == null) {
            return false;
        }
        return dataFim.isAfter(dataInicio);
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
            status = StatusOferta.PLANEJADA;
        }
        if (vagasOcupadas == null) {
            vagasOcupadas = 0;
        }
        if (listaEspera == null) {
            listaEspera = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
