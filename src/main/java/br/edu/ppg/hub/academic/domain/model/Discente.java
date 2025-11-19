package br.edu.ppg.hub.academic.domain.model;

import br.edu.ppg.hub.academic.domain.enums.StatusDiscente;
import br.edu.ppg.hub.academic.domain.enums.TipoCurso;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entidade que representa um discente (aluno de mestrado ou doutorado)
 *
 * @author PPG Hub
 * @since 1.0
 */
@Entity
@Table(
    name = "discentes",
    schema = "academic",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_discente_programa_matricula", columnNames = {"programa_id", "numero_matricula"})
    }
)
@EntityListeners(AuditingEntityListener.class)
@Check(constraints = "data_defesa IS NULL OR data_defesa >= data_ingresso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuário do sistema vinculado ao discente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_discente_usuario"))
    @NotNull(message = "Usuário é obrigatório")
    private Usuario usuario;

    /**
     * Programa ao qual o discente está vinculado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_discente_programa"))
    @NotNull(message = "Programa é obrigatório")
    private Programa programa;

    /**
     * Linha de pesquisa à qual o discente está vinculado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linha_pesquisa_id", foreignKey = @ForeignKey(name = "fk_discente_linha_pesquisa"))
    private LinhaPesquisa linhaPesquisa;

    /**
     * Orientador do discente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orientador_id", foreignKey = @ForeignKey(name = "fk_discente_orientador"))
    private Docente orientador;

    /**
     * Coorientador interno (do próprio programa)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coorientador_interno_id", foreignKey = @ForeignKey(name = "fk_discente_coorientador"))
    private Docente coorientadorInterno;

    /**
     * Número de matrícula do discente
     */
    @Column(name = "numero_matricula", nullable = false, length = 50)
    @NotBlank(message = "Número de matrícula é obrigatório")
    private String numeroMatricula;

    /**
     * Tipo de curso (Mestrado ou Doutorado)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_curso", nullable = false, length = 20)
    @NotNull(message = "Tipo de curso é obrigatório")
    private TipoCurso tipoCurso;

    /**
     * Turma/ano de ingresso
     */
    @Column(name = "turma", nullable = false)
    @NotNull(message = "Turma é obrigatória")
    private Integer turma;

    /**
     * Semestre de ingresso (ex: 2024.1, 2024.2)
     */
    @Column(name = "semestre_ingresso", nullable = false, length = 10)
    @NotBlank(message = "Semestre de ingresso é obrigatório")
    private String semestreIngresso;

    /**
     * Data de ingresso no programa
     */
    @Column(name = "data_ingresso", nullable = false)
    @NotNull(message = "Data de ingresso é obrigatória")
    private LocalDate dataIngresso;

    /**
     * Data da primeira matrícula em disciplinas
     */
    @Column(name = "data_primeira_matricula")
    private LocalDate dataPrimeiraMatricula;

    /**
     * Título do projeto de pesquisa
     */
    @Column(name = "titulo_projeto", columnDefinition = "TEXT")
    private String tituloProjeto;

    /**
     * Resumo do projeto
     */
    @Column(name = "resumo_projeto", columnDefinition = "TEXT")
    private String resumoProjeto;

    /**
     * Palavras-chave do projeto
     */
    @Column(name = "palavras_chave_projeto", columnDefinition = "TEXT")
    private String palavrasChaveProjeto;

    /**
     * Área CNPq do projeto
     */
    @Column(name = "area_cnpq", length = 255)
    private String areaCnpq;

    /**
     * Tipo de ingresso (Processo Seletivo, Convênio, etc)
     */
    @Column(name = "tipo_ingresso", length = 50)
    private String tipoIngresso;

    /**
     * Nota obtida no processo seletivo
     */
    @Column(name = "nota_processo_seletivo", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Nota não pode ser negativa")
    @DecimalMax(value = "100.0", message = "Nota não pode ser maior que 100")
    private BigDecimal notaProcessoSeletivo;

    /**
     * Classificação no processo seletivo
     */
    @Column(name = "classificacao_processo")
    @Min(value = 1, message = "Classificação deve ser maior que 0")
    private Integer classificacaoProcesso;

    // ==================================================
    // Coorientador Externo (não é da instituição)
    // ==================================================

    @Column(name = "coorientador_externo_nome", length = 255)
    private String coorientadorExternoNome;

    @Column(name = "coorientador_externo_instituicao", length = 255)
    private String coorientadorExternoInstituicao;

    @Column(name = "coorientador_externo_email", length = 255)
    @Email(message = "Email do coorientador externo inválido")
    private String coorientadorExternoEmail;

    @Column(name = "coorientador_externo_titulacao", length = 100)
    private String coorientadorExternoTitulacao;

    // ==================================================
    // Proficiência em Idioma Estrangeiro
    // ==================================================

    @Column(name = "proficiencia_idioma", length = 50)
    private String proficienciaIdioma;

    @Column(name = "proficiencia_status", length = 50)
    private String proficienciaStatus; // Aprovado, Pendente, Dispensado

    @Column(name = "data_proficiencia")
    private LocalDate dataProficiencia;

    @Column(name = "arquivo_proficiencia", columnDefinition = "TEXT")
    private String arquivoProficiencia;

    // ==================================================
    // Bolsa
    // ==================================================

    @Column(name = "bolsista")
    @Builder.Default
    private Boolean bolsista = false;

    @Column(name = "tipo_bolsa", length = 100)
    private String tipoBolsa;

    @Column(name = "modalidade_bolsa", length = 50)
    private String modalidadeBolsa;

    @Column(name = "valor_bolsa", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Valor da bolsa não pode ser negativo")
    private BigDecimal valorBolsa;

    @Column(name = "data_inicio_bolsa")
    private LocalDate dataInicioBolsa;

    @Column(name = "data_fim_bolsa")
    private LocalDate dataFimBolsa;

    @Column(name = "numero_processo_bolsa", length = 100)
    private String numeroProcessoBolsa;

    @Column(name = "agencia_fomento", length = 100)
    private String agenciaFomento;

    // ==================================================
    // Desempenho Acadêmico
    // ==================================================

    @Column(name = "creditos_necessarios")
    @Min(value = 0, message = "Créditos não podem ser negativos")
    private Integer creditosNecessarios;

    @Column(name = "coeficiente_rendimento", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Coeficiente não pode ser negativo")
    @DecimalMax(value = "10.0", message = "Coeficiente não pode ser maior que 10")
    private BigDecimal coeficienteRendimento;

    // ==================================================
    // Qualificação
    // ==================================================

    @Column(name = "qualificacao_realizada")
    @Builder.Default
    private Boolean qualificacaoRealizada = false;

    @Column(name = "data_qualificacao")
    private LocalDate dataQualificacao;

    @Column(name = "resultado_qualificacao", length = 50)
    private String resultadoQualificacao; // Aprovado, Reprovado, Aprovado com Restrições

    // ==================================================
    // Prazos e Prorrogações
    // ==================================================

    @Column(name = "prazo_original")
    private LocalDate prazoOriginal;

    /**
     * Histórico de prorrogações (array JSON)
     * Exemplo: [{"motivo": "...", "meses": 6, "data_aprovacao": "2024-01-01"}]
     */
    @Type(JsonType.class)
    @Column(name = "prorrogacoes", columnDefinition = "jsonb")
    @Builder.Default
    private List<Map<String, Object>> prorrogacoes = List.of();

    @Column(name = "data_limite_atual")
    private LocalDate dataLimiteAtual;

    // ==================================================
    // Defesa
    // ==================================================

    @Column(name = "data_defesa")
    private LocalDate dataDefesa;

    @Column(name = "resultado_defesa", length = 50)
    private String resultadoDefesa; // Aprovado, Reprovado, Aprovado com Correções

    @Column(name = "nota_defesa", precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "Nota não pode ser negativa")
    @DecimalMax(value = "10.0", message = "Nota não pode ser maior que 10")
    private BigDecimal notaDefesa;

    @Column(name = "titulo_final", columnDefinition = "TEXT")
    private String tituloFinal;

    /**
     * Documentos do discente (objeto JSON)
     * Exemplo: {"ata_qualificacao": "url", "projeto": "url", "tese": "url"}
     */
    @Type(JsonType.class)
    @Column(name = "documentos", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> documentos = new HashMap<>();

    // ==================================================
    // Status e Desligamento
    // ==================================================

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    @Builder.Default
    private StatusDiscente status = StatusDiscente.MATRICULADO;

    @Column(name = "motivo_desligamento", columnDefinition = "TEXT")
    private String motivoDesligamento;

    @Column(name = "data_desligamento")
    private LocalDate dataDesligamento;

    // ==================================================
    // Egresso
    // ==================================================

    @Column(name = "destino_egresso", columnDefinition = "TEXT")
    private String destinoEgresso;

    @Column(name = "atuacao_pos_formatura", columnDefinition = "TEXT")
    private String atuacaoPosFormatura;

    // ==================================================
    // Auditoria
    // ==================================================

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===========================
    // Métodos auxiliares
    // ===========================

    /**
     * Verifica se o discente está ativo no programa
     */
    public boolean isAtivo() {
        return status != null && status.isAtivo();
    }

    /**
     * Verifica se pode defender
     */
    public boolean podeDefender() {
        return status != null && status.podeDefender() &&
               Boolean.TRUE.equals(qualificacaoRealizada);
    }

    /**
     * Verifica se tem orientador definido
     */
    public boolean temOrientador() {
        return orientador != null;
    }

    /**
     * Verifica se tem coorientador (interno ou externo)
     */
    public boolean temCoorientador() {
        return coorientadorInterno != null ||
               (coorientadorExternoNome != null && !coorientadorExternoNome.isBlank());
    }

    /**
     * Verifica se está com bolsa vigente
     */
    public boolean temBolsaVigente() {
        if (!Boolean.TRUE.equals(bolsista)) {
            return false;
        }
        if (dataFimBolsa == null) {
            return true;
        }
        LocalDate hoje = LocalDate.now();
        return dataFimBolsa.isAfter(hoje) || dataFimBolsa.isEqual(hoje);
    }

    /**
     * Retorna o número total de prorrogações
     */
    public int getTotalProrrogacoes() {
        return prorrogacoes != null ? prorrogacoes.size() : 0;
    }

    /**
     * Verifica se o prazo está vencido
     */
    public boolean isPrazoVencido() {
        if (dataLimiteAtual == null) {
            return false;
        }
        return dataLimiteAtual.isBefore(LocalDate.now());
    }

    /**
     * Retorna o número de meses até o prazo limite
     */
    public long getMesesAtePrazo() {
        if (dataLimiteAtual == null) {
            return 0;
        }
        LocalDate hoje = LocalDate.now();
        if (dataLimiteAtual.isBefore(hoje)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.MONTHS.between(hoje, dataLimiteAtual);
    }

    /**
     * Verifica se já concluiu os créditos necessários
     */
    public boolean concluiuCreditos() {
        // Este método seria complementado com lógica que soma os créditos das disciplinas cursadas
        // Por ora, retorna false (seria implementado junto com MatriculaDisciplina)
        return false;
    }
}
