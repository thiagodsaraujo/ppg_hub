package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Docente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocenteResponse {

    private Long id;
    private Long instituicaoId;
    private String instituicaoNome;
    private String instituicaoSigla;

    // Dados Pessoais
    private String nomeCompleto;
    private String nomeCitacao;
    private String cpf;
    private String email;
    private String telefone;

    // Identificadores Acadêmicos
    private String lattesId;
    private String orcid;
    private String openalexAuthorId;
    private String scopusId;
    private String researcherId;

    // Formação
    private String titulacao;
    private String areaAtuacao;

    // Vínculo
    private String tipoVinculo;
    private String regimeTrabalho;
    private LocalDate dataIngresso;
    private LocalDate dataSaida;
    private Boolean ativo;

    // OpenAlex Metrics
    private Integer openalexWorksCount;
    private Integer openalexCitedByCount;
    private Integer openalexHIndex;
    private LocalDateTime openalexLastSyncAt;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
