package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para Banca.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BancaResponse {

    private Long id;

    // Relacionamentos
    private Long discenteId;
    private String discenteNome;
    private String discenteMatricula;

    private Long programaId;
    private String programaNome;
    private String programaSigla;

    // Dados da Banca
    private String tipoBanca;
    private LocalDateTime dataHora;
    private String localDefesa;
    private String tituloTrabalho;

    // Status e Resultado
    private String statusBanca;
    private String resultadoBanca;
    private LocalDateTime dataRealizacao;

    // Observações e Documentos
    private String observacoes;
    private String documentoAta;
    private String documentoTese;

    // Configurações
    private Boolean orientadorParticipa;
    private Boolean defensaRemota;
    private String linkVideoconferencia;

    // Membros
    private List<MembroBancaResponse> membros;

    // Estatísticas
    private Long numeroMembrosTitulares;
    private Long numeroMembrosExternos;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
