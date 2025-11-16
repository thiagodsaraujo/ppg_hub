package com.ppghub.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de Publicação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicacaoCreateRequest {

    // Identificadores
    @NotBlank(message = "OpenAlex Work ID é obrigatório")
    @Size(max = 50)
    private String openalexWorkId;

    @Size(max = 255)
    private String doi;

    @Size(max = 50)
    private String pmid;

    @Size(max = 50)
    private String pmcid;

    // Dados Básicos
    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String tituloOriginal;

    private String resumo;

    // Publicação
    @Min(value = 1900)
    @Max(value = 2100)
    private Integer anoPublicacao;

    private LocalDate dataPublicacao;

    @Size(max = 50)
    private String tipo;

    @Size(max = 10)
    private String idioma;

    // Fonte
    @Size(max = 500)
    private String fonteNome;

    @Size(max = 20)
    private String fonteIssn;

    @Size(max = 50)
    private String fonteOpenalexId;

    @Size(max = 50)
    private String volume;

    @Size(max = 50)
    private String issue;

    @Size(max = 20)
    private String paginaInicial;

    @Size(max = 20)
    private String paginaFinal;

    // Métricas
    private Integer citedByCount;
    private Boolean isRetracted;
    private Boolean isParatext;
    private Boolean isOa;

    // OpenAlex data (JSON strings)
    private String concepts;
    private String topics;
    private String keywords;

    // URLs
    @Size(max = 1000)
    private String landingPageUrl;

    @Size(max = 1000)
    private String pdfUrl;

    // Raw data
    private String rawOpenalexData;
}
