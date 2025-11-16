package com.ppghub.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para atualização de Banca.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BancaUpdateRequest {

    private LocalDateTime dataHora;

    @Size(max = 255)
    private String localDefesa;

    @Size(max = 500)
    private String tituloTrabalho;

    private String statusBanca; // AGENDADA, CONFIRMADA, REALIZADA, CANCELADA, REAGENDADA

    private String resultadoBanca; // APROVADO, APROVADO_COM_RESTRICOES, REPROVADO

    private String observacoes;

    @Size(max = 500)
    private String documentoAta;

    @Size(max = 500)
    private String documentoTese;

    private Boolean orientadorParticipa;

    private Boolean defensaRemota;

    @Size(max = 500)
    private String linkVideoconferencia;
}
