package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para Publicação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicacaoResponse {

    private Long id;

    // Identificadores
    private String openalexWorkId;
    private String doi;
    private String pmid;
    private String pmcid;

    // Dados Básicos
    private String titulo;
    private String tituloOriginal;
    private String resumo;

    // Publicação
    private Integer anoPublicacao;
    private LocalDate dataPublicacao;
    private String tipo;
    private String idioma;

    // Fonte
    private String fonteNome;
    private String fonteIssn;
    private String fonteOpenalexId;
    private String volume;
    private String issue;
    private String paginaInicial;
    private String paginaFinal;

    // Métricas
    private Integer citedByCount;
    private Boolean isRetracted;
    private Boolean isParatext;
    private Boolean isOa;

    // URLs
    private String landingPageUrl;
    private String pdfUrl;

    // Autores
    private List<AutoriaResponse> autores;

    // Sync
    private LocalDateTime lastSyncAt;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
