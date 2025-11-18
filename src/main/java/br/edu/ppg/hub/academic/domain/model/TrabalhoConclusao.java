package br.edu.ppg.hub.academic.domain.model;

import br.edu.ppg.hub.academic.domain.enums.StatusTrabalho;
import br.edu.ppg.hub.academic.domain.enums.TipoTrabalho;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um trabalho de conclusão (dissertação ou tese).
 * Armazena informações sobre dissertações de mestrado, teses de doutorado e outros trabalhos acadêmicos.
 *
 * <p>Inclui informações sobre:
 * <ul>
 *   <li>Dados bibliográficos (título, resumo, palavras-chave)</li>
 *   <li>Orientação (orientador e coorientador)</li>
 *   <li>Defesa (data, local, banca)</li>
 *   <li>Publicação (arquivo PDF, repositório institucional)</li>
 *   <li>Métricas (downloads, visualizações, citações)</li>
 * </ul>
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Entity
@Table(
    name = "trabalhos_conclusao",
    schema = "academic",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_trabalho_discente", columnNames = {"discente_id"})
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrabalhoConclusao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTOS ====================

    /**
     * Discente autor do trabalho
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discente_id", nullable = false)
    @NotNull(message = "O discente é obrigatório")
    private Discente discente;

    /**
     * Docente orientador do trabalho
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orientador_id", nullable = false)
    @NotNull(message = "O orientador é obrigatório")
    private Docente orientador;

    /**
     * Docente coorientador do trabalho (opcional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coorientador_id")
    private Docente coorientador;

    // ==================== DADOS BIBLIOGRÁFICOS ====================

    /**
     * Tipo do trabalho (Dissertação, Tese, Artigo, Projeto)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 50, nullable = false)
    @NotNull(message = "O tipo do trabalho é obrigatório")
    private TipoTrabalho tipo;

    /**
     * Título do trabalho em português
     */
    @Column(name = "titulo_portugues", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "O título em português é obrigatório")
    @Size(max = 1000, message = "O título não pode exceder 1000 caracteres")
    private String tituloPortugues;

    /**
     * Título do trabalho em inglês
     */
    @Column(name = "titulo_ingles", columnDefinition = "TEXT")
    @Size(max = 1000, message = "O título em inglês não pode exceder 1000 caracteres")
    private String tituloIngles;

    /**
     * Subtítulo do trabalho (opcional)
     */
    @Column(name = "subtitulo", columnDefinition = "TEXT")
    @Size(max = 500, message = "O subtítulo não pode exceder 500 caracteres")
    private String subtitulo;

    /**
     * Resumo do trabalho em português
     */
    @Column(name = "resumo_portugues", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "O resumo em português é obrigatório")
    @Size(max = 5000, message = "O resumo não pode exceder 5000 caracteres")
    private String resumoPortugues;

    /**
     * Resumo do trabalho em inglês
     */
    @Column(name = "resumo_ingles", columnDefinition = "TEXT")
    @Size(max = 5000, message = "O resumo em inglês não pode exceder 5000 caracteres")
    private String resumoIngles;

    /**
     * Abstract (geralmente em inglês)
     */
    @Column(name = "abstract", columnDefinition = "TEXT")
    @Size(max = 5000, message = "O abstract não pode exceder 5000 caracteres")
    private String abstractText;

    /**
     * Palavras-chave em português (separadas por ponto e vírgula)
     */
    @Column(name = "palavras_chave_portugues", columnDefinition = "TEXT")
    @Size(max = 500, message = "As palavras-chave não podem exceder 500 caracteres")
    private String palavrasChavePortugues;

    /**
     * Palavras-chave em inglês (separadas por ponto e vírgula)
     */
    @Column(name = "palavras_chave_ingles", columnDefinition = "TEXT")
    @Size(max = 500, message = "As palavras-chave em inglês não podem exceder 500 caracteres")
    private String palavrasChaveIngles;

    /**
     * Keywords (geralmente em inglês, separadas por ponto e vírgula)
     */
    @Column(name = "keywords", columnDefinition = "TEXT")
    @Size(max = 500, message = "As keywords não podem exceder 500 caracteres")
    private String keywords;

    // ==================== CLASSIFICAÇÃO ====================

    /**
     * Área CNPq do trabalho
     */
    @Column(name = "area_cnpq", columnDefinition = "TEXT")
    @Size(max = 200, message = "A área CNPq não pode exceder 200 caracteres")
    private String areaCnpq;

    /**
     * Área de concentração do trabalho
     */
    @Column(name = "area_concentracao", columnDefinition = "TEXT")
    @Size(max = 200, message = "A área de concentração não pode exceder 200 caracteres")
    private String areaConcentracao;

    /**
     * Linha de pesquisa do trabalho
     */
    @Column(name = "linha_pesquisa", columnDefinition = "TEXT")
    @Size(max = 200, message = "A linha de pesquisa não pode exceder 200 caracteres")
    private String linhaPesquisa;

    // ==================== DEFESA ====================

    /**
     * Data da defesa do trabalho
     */
    @Column(name = "data_defesa")
    private LocalDate dataDefesa;

    /**
     * Ano da defesa
     */
    @Column(name = "ano_defesa")
    @Min(value = 1900, message = "O ano de defesa deve ser maior que 1900")
    @Max(value = 2100, message = "O ano de defesa deve ser menor que 2100")
    private Integer anoDefesa;

    /**
     * Semestre da defesa (1 ou 2)
     */
    @Column(name = "semestre_defesa")
    @Min(value = 1, message = "O semestre deve ser 1 ou 2")
    @Max(value = 2, message = "O semestre deve ser 1 ou 2")
    private Integer semestreDefesa;

    /**
     * Local onde foi realizada a defesa
     */
    @Column(name = "local_defesa", columnDefinition = "TEXT")
    @Size(max = 500, message = "O local da defesa não pode exceder 500 caracteres")
    private String localDefesa;

    // ==================== ARQUIVO E PUBLICAÇÃO ====================

    /**
     * Número de páginas do trabalho
     */
    @Column(name = "numero_paginas")
    @Min(value = 1, message = "O número de páginas deve ser maior que zero")
    private Integer numeroPaginas;

    /**
     * Idioma principal do trabalho (pt, en, es, etc.)
     */
    @Column(name = "idioma", length = 10)
    @Pattern(regexp = "^[a-z]{2}$", message = "O idioma deve ter 2 letras minúsculas (ISO 639-1)")
    @Builder.Default
    private String idioma = "pt";

    /**
     * URL ou caminho do arquivo PDF do trabalho
     */
    @Column(name = "arquivo_pdf", columnDefinition = "TEXT")
    private String arquivoPdf;

    /**
     * URL ou caminho do arquivo da ata de defesa
     */
    @Column(name = "arquivo_ata_defesa", columnDefinition = "TEXT")
    private String arquivoAtaDefesa;

    /**
     * Tamanho do arquivo em bytes
     */
    @Column(name = "tamanho_arquivo_bytes")
    @Min(value = 0, message = "O tamanho do arquivo não pode ser negativo")
    private Long tamanhoArquivoBytes;

    /**
     * Handle URI do repositório institucional
     */
    @Column(name = "handle_uri", columnDefinition = "TEXT")
    private String handleUri;

    /**
     * URI completa no repositório institucional
     */
    @Column(name = "uri_repositorio", columnDefinition = "TEXT")
    private String uriRepositorio;

    /**
     * URL para download do trabalho
     */
    @Column(name = "url_download", columnDefinition = "TEXT")
    private String urlDownload;

    /**
     * Tipo de acesso ao trabalho (Acesso Aberto, Restrito, Embargado)
     */
    @Column(name = "tipo_acesso", length = 50)
    @Builder.Default
    private String tipoAcesso = "Acesso Aberto";

    // ==================== IDENTIFICADORES EXTERNOS ====================

    /**
     * ID do trabalho no OpenAlex
     */
    @Column(name = "openalex_work_id", columnDefinition = "TEXT")
    private String openalexWorkId;

    /**
     * DOI (Digital Object Identifier) do trabalho
     */
    @Column(name = "doi", columnDefinition = "TEXT")
    @Pattern(regexp = "^10\\.\\d{4,}/.+$", message = "DOI inválido")
    private String doi;

    /**
     * ISBN do trabalho (se aplicável)
     */
    @Column(name = "isbn", columnDefinition = "TEXT")
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$", message = "ISBN inválido")
    private String isbn;

    // ==================== MÉTRICAS ====================

    /**
     * Contador de downloads do trabalho
     */
    @Column(name = "downloads_count")
    @Builder.Default
    private Integer downloadsCount = 0;

    /**
     * Contador de visualizações do trabalho
     */
    @Column(name = "visualizacoes_count")
    @Builder.Default
    private Integer visualizacoesCount = 0;

    /**
     * Contador de citações do trabalho
     */
    @Column(name = "citacoes_count")
    @Builder.Default
    private Integer citacoesCount = 0;

    // ==================== AVALIAÇÃO ====================

    /**
     * Nota de avaliação do trabalho (0.00 a 10.00)
     */
    @Column(name = "nota_avaliacao", precision = 4, scale = 2)
    @DecimalMin(value = "0.00", message = "A nota deve ser maior ou igual a 0")
    @DecimalMax(value = "10.00", message = "A nota deve ser menor ou igual a 10")
    private BigDecimal notaAvaliacao;

    /**
     * Prêmios e reconhecimentos recebidos pelo trabalho
     */
    @Column(name = "premios_reconhecimentos", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Os prêmios e reconhecimentos não podem exceder 2000 caracteres")
    private String premiosReconhecimentos;

    // ==================== STATUS ====================

    /**
     * Status atual do trabalho
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    @NotNull(message = "O status do trabalho é obrigatório")
    @Builder.Default
    private StatusTrabalho status = StatusTrabalho.EM_PREPARACAO;

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
     * Verifica se o trabalho pode ser defendido.
     * Um trabalho pode ser defendido se estiver com status QUALIFICADO.
     *
     * @return true se pode ser defendido, false caso contrário
     */
    public boolean podeDefender() {
        return this.status == StatusTrabalho.QUALIFICADO;
    }

    /**
     * Verifica se o trabalho tem orientador definido.
     *
     * @return true se tem orientador, false caso contrário
     */
    public boolean temOrientador() {
        return this.orientador != null;
    }

    /**
     * Verifica se o trabalho tem coorientador definido.
     *
     * @return true se tem coorientador, false caso contrário
     */
    public boolean temCoorientador() {
        return this.coorientador != null;
    }

    /**
     * Verifica se o trabalho está publicado no repositório institucional.
     *
     * @return true se está publicado, false caso contrário
     */
    public boolean isPublicado() {
        return this.status == StatusTrabalho.PUBLICADO;
    }

    /**
     * Verifica se o trabalho tem arquivo PDF anexado.
     *
     * @return true se tem arquivo, false caso contrário
     */
    public boolean temArquivo() {
        return this.arquivoPdf != null && !this.arquivoPdf.isEmpty();
    }

    /**
     * Verifica se o trabalho já foi defendido.
     *
     * @return true se já foi defendido, false caso contrário
     */
    public boolean foiDefendido() {
        return this.dataDefesa != null;
    }

    /**
     * Verifica se o trabalho está pronto para submissão.
     * Considera obrigatórios: título, resumo e orientador.
     *
     * @return true se está pronto para submissão, false caso contrário
     */
    public boolean prontoParaSubmissao() {
        return this.tituloPortugues != null && !this.tituloPortugues.isEmpty()
            && this.resumoPortugues != null && !this.resumoPortugues.isEmpty()
            && this.orientador != null;
    }

    /**
     * Incrementa o contador de downloads.
     */
    public void incrementarDownloads() {
        if (this.downloadsCount == null) {
            this.downloadsCount = 0;
        }
        this.downloadsCount++;
    }

    /**
     * Incrementa o contador de visualizações.
     */
    public void incrementarVisualizacoes() {
        if (this.visualizacoesCount == null) {
            this.visualizacoesCount = 0;
        }
        this.visualizacoesCount++;
    }

    /**
     * Incrementa o contador de citações.
     */
    public void incrementarCitacoes() {
        if (this.citacoesCount == null) {
            this.citacoesCount = 0;
        }
        this.citacoesCount++;
    }
}
