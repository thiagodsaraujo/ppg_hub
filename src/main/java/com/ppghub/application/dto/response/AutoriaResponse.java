package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Autoria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoriaResponse {

    private Long id;
    private Long publicacaoId;
    private Long docenteId;
    private String docenteNome;

    // Posição na autoria
    private String authorPosition;
    private Integer positionOrder;
    private Boolean isCorresponding;

    // Dados brutos (quando não há docente associado)
    private String rawAuthorName;
    private String rawAuthorOpenalexId;
    private String rawAuthorOrcid;
}
