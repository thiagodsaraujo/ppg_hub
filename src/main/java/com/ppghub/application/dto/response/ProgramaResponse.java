package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Programa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaResponse {

    private Long id;
    private Long instituicaoId;
    private String instituicaoNome;
    private String instituicaoSigla;

    private String nome;
    private String sigla;
    private String codigoCapes;

    // Classificação
    private String areaConhecimento;
    private String areaAvaliacao;
    private String modalidade;
    private String nivel;

    // Avaliação CAPES
    private Integer conceitoCapes;
    private Integer anoAvaliacao;

    // Contato
    private String coordenador;
    private String email;
    private String telefone;
    private String website;

    // Status
    private String status;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
