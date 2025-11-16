package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Membro de Banca.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembroBancaResponse {

    private Long id;

    private Long bancaId;

    // Docente ou Professor Externo
    private Long docenteId;
    private String docenteNome;
    private String docenteInstituicao;

    private Long professorExternoId;
    private String professorExternoNome;
    private String professorExternoInstituicao;

    // Tipo e Função
    private String tipoMembro;
    private String funcao;

    // Status do Convite
    private String statusConvite;
    private LocalDateTime dataConvite;
    private LocalDateTime dataResposta;

    // Observações
    private String observacoes;
    private Integer ordemApresentacao;

    // Flags
    private Boolean externo;
    private Boolean confirmado;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
