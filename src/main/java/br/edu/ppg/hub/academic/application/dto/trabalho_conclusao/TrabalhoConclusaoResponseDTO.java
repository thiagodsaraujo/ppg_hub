package br.edu.ppg.hub.academic.application.dto.trabalho_conclusao;

import br.edu.ppg.hub.academic.domain.enums.StatusTrabalho;
import br.edu.ppg.hub.academic.domain.enums.TipoTrabalho;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para um trabalho de conclusão.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrabalhoConclusaoResponseDTO {

    private Long id;

    // IDs de relacionamentos
    private Long discenteId;
    private String discenteNome;

    private Long orientadorId;
    private String orientadorNome;

    private Long coorientadorId;
    private String coorientadorNome;

    // Dados bibliográficos
    private TipoTrabalho tipo;
    private String tituloPortugues;
    private String tituloIngles;
    private String subtitulo;
    private String resumoPortugues;
    private String resumoIngles;
    private String abstractText;
    private String palavrasChavePortugues;
    private String palavrasChaveIngles;
    private String keywords;

    // Classificação
    private String areaCnpq;
    private String areaConcentracao;
    private String linhaPesquisa;

    // Defesa
    private LocalDate dataDefesa;
    private Integer anoDefesa;
    private Integer semestreDefesa;
    private String localDefesa;

    // Arquivo e publicação
    private Integer numeroPaginas;
    private String idioma;
    private String arquivoPdf;
    private String arquivoAtaDefesa;
    private Long tamanhoArquivoBytes;
    private String handleUri;
    private String uriRepositorio;
    private String urlDownload;
    private String tipoAcesso;

    // Identificadores externos
    private String openalexWorkId;
    private String doi;
    private String isbn;

    // Métricas
    private Integer downloadsCount;
    private Integer visualizacoesCount;
    private Integer citacoesCount;

    // Avaliação
    private BigDecimal notaAvaliacao;
    private String premiosReconhecimentos;

    // Status
    private StatusTrabalho status;

    // Auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
