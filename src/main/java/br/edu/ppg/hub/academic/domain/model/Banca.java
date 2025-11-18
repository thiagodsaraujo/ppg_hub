package br.edu.ppg.hub.academic.domain.model;

import br.edu.ppg.hub.academic.domain.enums.ResultadoBanca;
import br.edu.ppg.hub.academic.domain.enums.TipoBanca;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * Entidade que representa uma banca examinadora (qualificação ou defesa).
 * Armazena informações sobre bancas de qualificação, defesa de dissertação e defesa de tese.
 *
 * <p>Inclui informações sobre:
 * <ul>
 *   <li>Agendamento (data, horário, local, modalidade)</li>
 *   <li>Composição (presidente, secretário, membros)</li>
 *   <li>Resultado (aprovação, nota, correções exigidas)</li>
 *   <li>Documentação (ata, pauta, recomendações)</li>
 * </ul>
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Entity
@Table(
    name = "bancas",
    schema = "academic"
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTOS ====================

    /**
     * Trabalho de conclusão associado à banca (pode ser null para qualificação)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabalho_conclusao_id")
    private TrabalhoConclusao trabalhoConclusao;

    /**
     * Discente que será avaliado pela banca
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discente_id", nullable = false)
    @NotNull(message = "O discente é obrigatório")
    private Discente discente;

    /**
     * Docente presidente da banca
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presidente_id", nullable = false)
    @NotNull(message = "O presidente da banca é obrigatório")
    private Docente presidente;

    /**
     * Docente secretário da banca (opcional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secretario_id")
    private Docente secretario;

    // ==================== TIPO E STATUS ====================

    /**
     * Tipo da banca (Qualificação, Defesa de Dissertação, Defesa de Tese)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 50, nullable = false)
    @NotNull(message = "O tipo da banca é obrigatório")
    private TipoBanca tipo;

    /**
     * Status da banca (Agendada, Realizada, Cancelada, Adiada)
     */
    @Column(name = "status", length = 50, nullable = false)
    @Builder.Default
    private String status = "Agendada";

    // ==================== AGENDAMENTO ====================

    /**
     * Data agendada para a realização da banca
     */
    @Column(name = "data_agendada", nullable = false)
    @NotNull(message = "A data agendada é obrigatória")
    @Future(message = "A data agendada deve ser futura")
    private LocalDate dataAgendada;

    /**
     * Horário de início da banca
     */
    @Column(name = "horario_inicio", nullable = false)
    @NotNull(message = "O horário de início é obrigatório")
    private LocalTime horarioInicio;

    /**
     * Horário de término da banca
     */
    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    /**
     * Local onde será realizada a banca
     */
    @Column(name = "local_realizacao", columnDefinition = "TEXT")
    @Size(max = 500, message = "O local de realização não pode exceder 500 caracteres")
    private String localRealizacao;

    /**
     * Modalidade da banca (Presencial, Virtual, Híbrida)
     */
    @Column(name = "modalidade", length = 50)
    @Builder.Default
    private String modalidade = "Presencial";

    /**
     * Link para participação virtual (se modalidade for Virtual ou Híbrida)
     */
    @Column(name = "link_virtual", columnDefinition = "TEXT")
    private String linkVirtual;

    /**
     * Data efetiva de realização da banca (pode diferir da agendada)
     */
    @Column(name = "data_realizacao")
    private LocalDate dataRealizacao;

    // ==================== DOCUMENTAÇÃO ====================

    /**
     * Número da ata de defesa/qualificação
     */
    @Column(name = "ata_numero", length = 50)
    @Size(max = 50, message = "O número da ata não pode exceder 50 caracteres")
    private String ataNumero;

    /**
     * URL ou caminho do arquivo da ata
     */
    @Column(name = "ata_arquivo", columnDefinition = "TEXT")
    private String ataArquivo;

    /**
     * Pauta da banca em formato JSON
     * Exemplo: {"itens": ["Apresentação do trabalho", "Arguição", "Deliberação"]}
     */
    @Type(JsonType.class)
    @Column(name = "pauta", columnDefinition = "jsonb")
    private Map<String, Object> pauta;

    /**
     * Ata da banca em formato JSON
     * Exemplo: {"inicio": "14:00", "termino": "17:00", "deliberacao": "Aprovado"}
     */
    @Type(JsonType.class)
    @Column(name = "ata", columnDefinition = "jsonb")
    private Map<String, Object> ata;

    // ==================== RESULTADO ====================

    /**
     * Resultado da banca
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", length = 50)
    private ResultadoBanca resultado;

    /**
     * Nota final atribuída pela banca (0.00 a 10.00)
     */
    @Column(name = "nota_final", precision = 4, scale = 2)
    @DecimalMin(value = "0.00", message = "A nota deve ser maior ou igual a 0")
    @DecimalMax(value = "10.00", message = "A nota deve ser menor ou igual a 10")
    private BigDecimal notaFinal;

    /**
     * Prazo em dias para realizar as correções exigidas
     */
    @Column(name = "prazo_correcoes_dias")
    @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia")
    @Max(value = 365, message = "O prazo não pode exceder 365 dias")
    private Integer prazoCorrecoesDias;

    /**
     * Descrição das correções exigidas pela banca
     */
    @Column(name = "correcoes_exigidas", columnDefinition = "TEXT")
    @Size(max = 5000, message = "As correções exigidas não podem exceder 5000 caracteres")
    private String correcoesExigidas;

    /**
     * Observações gerais da banca
     */
    @Column(name = "observacoes_banca", columnDefinition = "TEXT")
    @Size(max = 5000, message = "As observações não podem exceder 5000 caracteres")
    private String observacoesBanca;

    /**
     * Recomendações da banca para publicação ou melhorias
     */
    @Column(name = "recomendacoes", columnDefinition = "TEXT")
    @Size(max = 5000, message = "As recomendações não podem exceder 5000 caracteres")
    private String recomendacoes;

    /**
     * Indica se a banca recomenda a publicação do trabalho
     */
    @Column(name = "sugestao_publicacao")
    @Builder.Default
    private Boolean sugestaoPublicacao = false;

    // ==================== AUDITORIA ====================

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

    // ==================== MÉTODOS DE NEGÓCIO ====================

    /**
     * Verifica se a banca já foi realizada.
     *
     * @return true se foi realizada, false caso contrário
     */
    public boolean isRealizada() {
        return "Realizada".equals(this.status);
    }

    /**
     * Verifica se a banca está agendada.
     *
     * @return true se está agendada, false caso contrário
     */
    public boolean isAgendada() {
        return "Agendada".equals(this.status);
    }

    /**
     * Verifica se a banca foi cancelada.
     *
     * @return true se foi cancelada, false caso contrário
     */
    public boolean isCancelada() {
        return "Cancelada".equals(this.status);
    }

    /**
     * Verifica se a banca pode ser realizada.
     * Uma banca pode ser realizada se estiver agendada e a data for hoje ou passada.
     *
     * @return true se pode ser realizada, false caso contrário
     */
    public boolean podeRealizar() {
        return this.isAgendada() &&
               (this.dataAgendada.equals(LocalDate.now()) || this.dataAgendada.isBefore(LocalDate.now()));
    }

    /**
     * Verifica se a banca pode ser cancelada.
     * Uma banca pode ser cancelada se estiver agendada.
     *
     * @return true se pode ser cancelada, false caso contrário
     */
    public boolean podeCancelar() {
        return this.isAgendada();
    }

    /**
     * Verifica se a banca pode ser reagendada.
     * Uma banca pode ser reagendada se estiver agendada ou adiada.
     *
     * @return true se pode ser reagendada, false caso contrário
     */
    public boolean podeReagendar() {
        return "Agendada".equals(this.status) || "Adiada".equals(this.status);
    }

    /**
     * Verifica se a banca é virtual.
     *
     * @return true se for virtual, false caso contrário
     */
    public boolean isVirtual() {
        return "Virtual".equals(this.modalidade);
    }

    /**
     * Verifica se a banca é híbrida.
     *
     * @return true se for híbrida, false caso contrário
     */
    public boolean isHibrida() {
        return "Híbrida".equals(this.modalidade);
    }

    /**
     * Verifica se a banca requer link virtual.
     *
     * @return true se requer link virtual, false caso contrário
     */
    public boolean requerLinkVirtual() {
        return this.isVirtual() || this.isHibrida();
    }

    /**
     * Verifica se a banca tem resultado definido.
     *
     * @return true se tem resultado, false caso contrário
     */
    public boolean temResultado() {
        return this.resultado != null;
    }

    /**
     * Verifica se o candidato foi aprovado.
     *
     * @return true se foi aprovado, false caso contrário
     */
    public boolean isAprovado() {
        return this.resultado != null && this.resultado.isAprovado();
    }

    /**
     * Verifica se foram exigidas correções.
     *
     * @return true se foram exigidas correções, false caso contrário
     */
    public boolean exigeCorrecoes() {
        return this.resultado != null && this.resultado.exigeCorrecoes();
    }

    /**
     * Verifica se a banca é de qualificação.
     *
     * @return true se for qualificação, false caso contrário
     */
    public boolean isQualificacao() {
        return this.tipo == TipoBanca.QUALIFICACAO;
    }

    /**
     * Verifica se a banca é de defesa.
     *
     * @return true se for defesa, false caso contrário
     */
    public boolean isDefesa() {
        return this.tipo != null && this.tipo.isDefesa();
    }

    /**
     * Calcula o resultado final da banca baseado nas notas dos membros.
     * Este método é um placeholder - a lógica real deve ser implementada no service.
     *
     * @return ResultadoBanca calculado
     */
    public ResultadoBanca calcularResultadoFinal() {
        // Implementação básica - a lógica real deve estar no service
        if (this.notaFinal != null) {
            if (this.notaFinal.compareTo(new BigDecimal("7.0")) >= 0) {
                return ResultadoBanca.APROVADO;
            } else {
                return ResultadoBanca.REPROVADO;
            }
        }
        return null;
    }

    /**
     * Verifica se a banca tem ata registrada.
     *
     * @return true se tem ata, false caso contrário
     */
    public boolean temAta() {
        return this.ataArquivo != null && !this.ataArquivo.isEmpty();
    }

    /**
     * Verifica se a banca tem pauta definida.
     *
     * @return true se tem pauta, false caso contrário
     */
    public boolean temPauta() {
        return this.pauta != null && !this.pauta.isEmpty();
    }
}
