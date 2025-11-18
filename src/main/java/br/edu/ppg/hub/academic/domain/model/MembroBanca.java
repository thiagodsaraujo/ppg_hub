package br.edu.ppg.hub.academic.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um membro de uma banca examinadora.
 * Um membro pode ser um docente interno ou um avaliador externo.
 *
 * <p>Inclui informações sobre:
 * <ul>
 *   <li>Identificação (docente interno ou dados do externo)</li>
 *   <li>Função (Presidente, Examinador Interno, Examinador Externo, Suplente)</li>
 *   <li>Participação (confirmação, presença, justificativa de ausência)</li>
 *   <li>Avaliação (nota individual, parecer)</li>
 * </ul>
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Entity
@Table(
    name = "membros_banca",
    schema = "academic"
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembroBanca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTOS ====================

    /**
     * Banca à qual o membro pertence
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banca_id", nullable = false)
    @NotNull(message = "A banca é obrigatória")
    private Banca banca;

    /**
     * Docente interno que é membro da banca (null se for membro externo)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    // ==================== DADOS DO MEMBRO ====================

    /**
     * Nome completo do membro (obrigatório para membros externos)
     */
    @Column(name = "nome_completo", length = 255)
    @Size(max = 255, message = "O nome completo não pode exceder 255 caracteres")
    private String nomeCompleto;

    /**
     * Instituição do membro (obrigatório para membros externos)
     */
    @Column(name = "instituicao", length = 255)
    @Size(max = 255, message = "A instituição não pode exceder 255 caracteres")
    private String instituicao;

    /**
     * Titulação do membro (ex: Doutor, Mestre, Pós-Doutor)
     */
    @Column(name = "titulacao", length = 100)
    @Size(max = 100, message = "A titulação não pode exceder 100 caracteres")
    private String titulacao;

    /**
     * Email do membro
     */
    @Column(name = "email", length = 255)
    @Email(message = "Email inválido")
    @Size(max = 255, message = "O email não pode exceder 255 caracteres")
    private String email;

    /**
     * Resumo do currículo do membro (Lattes resumido)
     */
    @Column(name = "curriculo_resumo", columnDefinition = "TEXT")
    @Size(max = 2000, message = "O currículo resumido não pode exceder 2000 caracteres")
    private String curriculoResumo;

    // ==================== FUNÇÃO E TIPO ====================

    /**
     * Função do membro na banca
     * Valores possíveis: Presidente, Examinador_Interno, Examinador_Externo, Suplente
     */
    @Column(name = "funcao", length = 50, nullable = false)
    @NotBlank(message = "A função é obrigatória")
    private String funcao;

    /**
     * Tipo do membro (Interno ou Externo)
     */
    @Column(name = "tipo", length = 50, nullable = false)
    @NotBlank(message = "O tipo é obrigatório")
    private String tipo;

    /**
     * Ordem de apresentação do membro na banca (para arguição)
     */
    @Column(name = "ordem_apresentacao")
    @Min(value = 1, message = "A ordem de apresentação deve ser maior que zero")
    private Integer ordemApresentacao;

    // ==================== PARTICIPAÇÃO ====================

    /**
     * Indica se o membro confirmou participação
     */
    @Column(name = "confirmado")
    @Builder.Default
    private Boolean confirmado = false;

    /**
     * Indica se o membro esteve presente na banca
     */
    @Column(name = "presente")
    private Boolean presente;

    /**
     * Justificativa em caso de ausência
     */
    @Column(name = "justificativa_ausencia", columnDefinition = "TEXT")
    @Size(max = 1000, message = "A justificativa de ausência não pode exceder 1000 caracteres")
    private String justificativaAusencia;

    // ==================== AVALIAÇÃO ====================

    /**
     * Parecer individual do membro sobre o trabalho
     */
    @Column(name = "parecer_individual", columnDefinition = "TEXT")
    @Size(max = 5000, message = "O parecer individual não pode exceder 5000 caracteres")
    private String parecerIndividual;

    /**
     * Nota individual atribuída pelo membro (0.00 a 10.00)
     */
    @Column(name = "nota_individual", precision = 4, scale = 2)
    @DecimalMin(value = "0.00", message = "A nota deve ser maior ou igual a 0")
    @DecimalMax(value = "10.00", message = "A nota deve ser menor ou igual a 10")
    private BigDecimal notaIndividual;

    /**
     * URL ou caminho do arquivo do parecer (se enviado digitalmente)
     */
    @Column(name = "arquivo_parecer", columnDefinition = "TEXT")
    private String arquivoParecer;

    // ==================== AUDITORIA ====================

    /**
     * Data de criação do registro
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==================== MÉTODOS DE NEGÓCIO ====================

    /**
     * Verifica se o membro é presidente da banca.
     *
     * @return true se é presidente, false caso contrário
     */
    public boolean isPresidente() {
        return "Presidente".equals(this.funcao);
    }

    /**
     * Verifica se o membro é examinador interno.
     *
     * @return true se é examinador interno, false caso contrário
     */
    public boolean isExaminadorInterno() {
        return "Examinador_Interno".equals(this.funcao);
    }

    /**
     * Verifica se o membro é examinador externo.
     *
     * @return true se é examinador externo, false caso contrário
     */
    public boolean isExaminadorExterno() {
        return "Examinador_Externo".equals(this.funcao);
    }

    /**
     * Verifica se o membro é suplente.
     *
     * @return true se é suplente, false caso contrário
     */
    public boolean isSuplente() {
        return "Suplente".equals(this.funcao);
    }

    /**
     * Verifica se o membro é interno (docente da instituição).
     *
     * @return true se é interno, false caso contrário
     */
    public boolean isInterno() {
        return "Interno".equals(this.tipo) || this.docente != null;
    }

    /**
     * Verifica se o membro é externo (não é docente da instituição).
     *
     * @return true se é externo, false caso contrário
     */
    public boolean isExterno() {
        return "Externo".equals(this.tipo) || this.docente == null;
    }

    /**
     * Verifica se o membro confirmou participação.
     *
     * @return true se confirmou, false caso contrário
     */
    public boolean confirmouPresenca() {
        return Boolean.TRUE.equals(this.confirmado);
    }

    /**
     * Verifica se o membro esteve presente na banca.
     *
     * @return true se esteve presente, false caso contrário
     */
    public boolean estevePresente() {
        return Boolean.TRUE.equals(this.presente);
    }

    /**
     * Verifica se o membro atribuiu nota.
     *
     * @return true se atribuiu nota, false caso contrário
     */
    public boolean atribuiuNota() {
        return this.notaIndividual != null;
    }

    /**
     * Verifica se o membro emitiu parecer.
     *
     * @return true se emitiu parecer, false caso contrário
     */
    public boolean emitiuParecer() {
        return this.parecerIndividual != null && !this.parecerIndividual.isEmpty();
    }

    /**
     * Verifica se o membro enviou arquivo de parecer.
     *
     * @return true se enviou parecer, false caso contrário
     */
    public boolean enviouArquivoParecer() {
        return this.arquivoParecer != null && !this.arquivoParecer.isEmpty();
    }

    /**
     * Obtém o nome do membro (interno ou externo).
     *
     * @return Nome completo do membro
     */
    public String getNome() {
        if (this.docente != null) {
            return this.docente.getNomeCompleto();
        }
        return this.nomeCompleto;
    }

    /**
     * Obtém a instituição do membro (interno ou externo).
     *
     * @return Nome da instituição
     */
    public String getInstituicaoMembro() {
        if (this.docente != null && this.docente.getPrograma() != null) {
            return this.docente.getPrograma().getInstituicao();
        }
        return this.instituicao;
    }

    /**
     * Verifica se os dados do membro estão completos.
     *
     * @return true se os dados estão completos, false caso contrário
     */
    public boolean dadosCompletos() {
        if (this.isInterno()) {
            return this.docente != null && this.funcao != null && this.tipo != null;
        } else {
            return this.nomeCompleto != null && !this.nomeCompleto.isEmpty()
                && this.instituicao != null && !this.instituicao.isEmpty()
                && this.funcao != null && this.tipo != null;
        }
    }

    /**
     * Verifica se o membro pode ser removido da banca.
     * Um membro pode ser removido se a banca ainda não foi realizada.
     *
     * @return true se pode ser removido, false caso contrário
     */
    public boolean podeSerRemovido() {
        return this.banca != null && this.banca.isAgendada();
    }
}
