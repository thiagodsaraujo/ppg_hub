package com.ppghub.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de Membro de Banca.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembroBancaCreateRequest {

    // Exatamente um destes deve ser preenchido
    private Long docenteId;
    private Long professorExternoId;

    @NotBlank(message = "Tipo de membro é obrigatório")
    private String tipoMembro; // TITULAR, SUPLENTE

    @NotBlank(message = "Função é obrigatória")
    private String funcao; // PRESIDENTE, MEMBRO_INTERNO, MEMBRO_EXTERNO, ORIENTADOR, COORIENTADOR

    private String statusConvite; // PENDENTE, ENVIADO, CONFIRMADO, RECUSADO

    private String observacoes;

    private Integer ordemApresentacao;
}
