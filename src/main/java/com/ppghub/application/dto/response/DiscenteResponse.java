package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Discente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscenteResponse {

    private Long id;

    private String matricula;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private LocalDate dataNascimento;

    // Vínculo Acadêmico
    private Long programaId;
    private String programaNome;
    private String programaSigla;

    private Long orientadorId;
    private String orientadorNome;

    private LocalDate dataIngresso;
    private LocalDate dataConclusao;
    private String statusMatricula;
    private String nivelFormacao;

    // Dados da Tese/Dissertação
    private String tituloTese;
    private LocalDate dataDefesa;

    // Identificadores Acadêmicos
    private String lattesId;
    private String orcid;
    private String openalexAuthorId;

    // OpenAlex Metrics
    private Integer openalexWorksCount;
    private Integer openalexCitedByCount;
    private LocalDateTime openalexLastSyncAt;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
