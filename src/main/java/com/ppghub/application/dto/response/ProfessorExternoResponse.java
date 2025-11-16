package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Professor Externo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorExternoResponse {

    private Long id;

    private String nome;
    private String email;
    private String instituicaoOrigem;
    private String titulacao;
    private String especialidade;
    private String telefone;

    // Identificadores Acadêmicos
    private String lattesId;
    private String orcid;
    private String openalexAuthorId;
    private String scopusId;

    // OpenAlex Metrics
    private Integer openalexWorksCount;
    private Integer openalexCitedByCount;
    private Integer openalexHIndex;
    private LocalDateTime openalexLastSyncAt;

    // Status
    private String observacoes;
    private Boolean validado;
    private Boolean ativo;

    // Estatísticas
    private Integer numeroParticipacoes;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
