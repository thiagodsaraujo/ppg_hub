package br.edu.ppg.hub.academic.domain.model;

import br.edu.ppg.hub.academic.domain.enums.CategoriaDocente;
import br.edu.ppg.hub.academic.domain.enums.RegimeTrabalho;
import br.edu.ppg.hub.academic.domain.enums.StatusDocente;
import br.edu.ppg.hub.academic.domain.enums.TipoVinculoDocente;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um docente vinculado a um programa de pós-graduação
 *
 * @author PPG Hub
 * @since 1.0
 */
@Entity
@Table(
    name = "docentes",
    schema = "academic",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_docente_usuario_programa", columnNames = {"usuario_id", "programa_id"})
    }
)
@EntityListeners(AuditingEntityListener.class)
@Check(constraints = "data_desvinculacao IS NULL OR data_desvinculacao >= data_vinculacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuário do sistema vinculado ao docente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_docente_usuario"))
    @NotNull(message = "Usuário é obrigatório")
    private Usuario usuario;

    /**
     * Programa ao qual o docente está vinculado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_docente_programa"))
    @NotNull(message = "Programa é obrigatório")
    private Programa programa;

    /**
     * Linha de pesquisa à qual o docente está vinculado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linha_pesquisa_id", foreignKey = @ForeignKey(name = "fk_docente_linha_pesquisa"))
    private LinhaPesquisa linhaPesquisa;

    /**
     * Matrícula do docente na instituição
     */
    @Column(name = "matricula", length = 50)
    private String matricula;

    /**
     * Categoria do docente (Titular, Associado, Adjunto, Assistente)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", length = 50)
    private CategoriaDocente categoria;

    /**
     * Regime de trabalho (DE, 40h, 20h)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "regime_trabalho", length = 50)
    private RegimeTrabalho regimeTrabalho;

    /**
     * Titulação máxima do docente (ex: Doutorado, Pós-Doutorado)
     */
    @Column(name = "titulacao_maxima", length = 100)
    private String titulacaoMaxima;

    /**
     * Instituição onde obteve a titulação máxima
     */
    @Column(name = "instituicao_titulacao", length = 255)
    private String instituicaoTitulacao;

    /**
     * Ano em que obteve a titulação máxima
     */
    @Column(name = "ano_titulacao")
    private Integer anoTitulacao;

    /**
     * País onde obteve a titulação
     */
    @Column(name = "pais_titulacao", length = 50)
    private String paisTitulacao;

    /**
     * Tipo de vínculo com o programa (Permanente, Colaborador, Visitante, Voluntário)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vinculo", nullable = false, length = 50)
    @NotNull(message = "Tipo de vínculo é obrigatório")
    private TipoVinculoDocente tipoVinculo;

    /**
     * Data de vinculação ao programa
     */
    @Column(name = "data_vinculacao", nullable = false)
    @NotNull(message = "Data de vinculação é obrigatória")
    private LocalDate dataVinculacao;

    /**
     * Data de desvinculação do programa (se aplicável)
     */
    @Column(name = "data_desvinculacao")
    private LocalDate dataDesvinculacao;

    /**
     * Número de orientações de mestrado em andamento
     */
    @Column(name = "orientacoes_mestrado_andamento")
    @Builder.Default
    @Min(value = 0, message = "Número de orientações não pode ser negativo")
    private Integer orientacoesMestradoAndamento = 0;

    /**
     * Número de orientações de doutorado em andamento
     */
    @Column(name = "orientacoes_doutorado_andamento")
    @Builder.Default
    @Min(value = 0, message = "Número de orientações não pode ser negativo")
    private Integer orientacoesDoutoradoAndamento = 0;

    /**
     * Número de orientações de mestrado concluídas
     */
    @Column(name = "orientacoes_mestrado_concluidas")
    @Builder.Default
    @Min(value = 0, message = "Número de orientações não pode ser negativo")
    private Integer orientacoesMestradoConcluidas = 0;

    /**
     * Número de orientações de doutorado concluídas
     */
    @Column(name = "orientacoes_doutorado_concluidas")
    @Builder.Default
    @Min(value = 0, message = "Número de orientações não pode ser negativo")
    private Integer orientacoesDoutoradoConcluidas = 0;

    /**
     * Número de coorientações realizadas
     */
    @Column(name = "coorientacoes")
    @Builder.Default
    @Min(value = 0, message = "Número de coorientações não pode ser negativo")
    private Integer coorientacoes = 0;

    /**
     * Indica se é bolsista de produtividade CNPq
     */
    @Column(name = "bolsista_produtividade")
    @Builder.Default
    private Boolean bolsistaProdutividade = false;

    /**
     * Nível da bolsa de produtividade (ex: 1A, 1B, 1C, 1D, 2)
     */
    @Column(name = "nivel_bolsa_produtividade", length = 20)
    private String nivelBolsaProdutividade;

    /**
     * Data de início da vigência da bolsa
     */
    @Column(name = "vigencia_bolsa_inicio")
    private LocalDate vigenciaBolsaInicio;

    /**
     * Data de fim da vigência da bolsa
     */
    @Column(name = "vigencia_bolsa_fim")
    private LocalDate vigenciaBolsaFim;

    /**
     * Áreas de interesse do docente
     */
    @Column(name = "areas_interesse", columnDefinition = "TEXT")
    private String areasInteresse;

    /**
     * Projetos de pesquisa atuais
     */
    @Column(name = "projetos_atuais", columnDefinition = "TEXT")
    private String projetosAtuais;

    /**
     * Resumo do currículo do docente
     */
    @Column(name = "curriculo_resumo", columnDefinition = "TEXT")
    private String curriculoResumo;

    /**
     * Status do docente (Ativo, Afastado, Aposentado, Desligado)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    @Builder.Default
    private StatusDocente status = StatusDocente.ATIVO;

    /**
     * Motivo do desligamento (se aplicável)
     */
    @Column(name = "motivo_desligamento", columnDefinition = "TEXT")
    private String motivoDesligamento;

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

    // ===========================
    // Métodos auxiliares
    // ===========================

    /**
     * Verifica se o docente está ativo
     */
    public boolean isAtivo() {
        return status != null && status.isVinculado();
    }

    /**
     * Verifica se é docente permanente
     */
    public boolean isPermanente() {
        return tipoVinculo != null && tipoVinculo.isPermanente();
    }

    /**
     * Verifica se pode orientar novos alunos
     */
    public boolean podeOrientar() {
        return isAtivo() && status.podeOrientar();
    }

    /**
     * Verifica se pode coorientar
     */
    public boolean podeCoorientar() {
        return isAtivo();
    }

    /**
     * Verifica se tem bolsa de produtividade vigente
     */
    public boolean temBolsaProdutividadeVigente() {
        if (!Boolean.TRUE.equals(bolsistaProdutividade)) {
            return false;
        }
        if (vigenciaBolsaFim == null) {
            return true;
        }
        return vigenciaBolsaFim.isAfter(LocalDate.now()) || vigenciaBolsaFim.isEqual(LocalDate.now());
    }

    /**
     * Retorna o total de orientações em andamento
     */
    public int getTotalOrientacoesAndamento() {
        return (orientacoesMestradoAndamento != null ? orientacoesMestradoAndamento : 0) +
               (orientacoesDoutoradoAndamento != null ? orientacoesDoutoradoAndamento : 0);
    }

    /**
     * Retorna o total de orientações concluídas
     */
    public int getTotalOrientacoesConcluidas() {
        return (orientacoesMestradoConcluidas != null ? orientacoesMestradoConcluidas : 0) +
               (orientacoesDoutoradoConcluidas != null ? orientacoesDoutoradoConcluidas : 0);
    }

    /**
     * Verifica se atingiu o limite de orientações simultâneas
     * (geralmente 8 para mestrado+doutorado, mas pode variar)
     */
    public boolean atingiuLimiteOrientacoes() {
        return getTotalOrientacoesAndamento() >= 8;
    }
}
