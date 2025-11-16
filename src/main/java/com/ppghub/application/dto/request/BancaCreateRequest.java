package com.ppghub.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para criação de Banca.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BancaCreateRequest {

    @NotNull(message = "ID do discente é obrigatório")
    private Long discenteId;

    @NotNull(message = "ID do programa é obrigatório")
    private Long programaId;

    @NotBlank(message = "Tipo de banca é obrigatório")
    private String tipoBanca; // QUALIFICACAO_MESTRADO, DEFESA_MESTRADO, etc

    @NotNull(message = "Data e hora são obrigatórias")
    private LocalDateTime dataHora;

    @Size(max = 255)
    private String localDefesa;

    @Size(max = 500)
    private String tituloTrabalho;

    private String statusBanca; // AGENDADA, CONFIRMADA, REALIZADA, CANCELADA

    private String observacoes;

    private Boolean orientadorParticipa;

    private Boolean defensaRemota;

    @Size(max = 500)
    private String linkVideoconferencia;

    // Membros da banca
    @Valid
    private List<MembroBancaCreateRequest> membros;
}
