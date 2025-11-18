package br.edu.ppg.hub.academic.application.dto.matricula_disciplina;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de uma nova matrícula em disciplina
 * DTO simplificado: apenas os IDs necessários
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDisciplinaCreateDTO {

    @NotNull(message = "ID da oferta de disciplina é obrigatório")
    private Long ofertaDisciplinaId;

    @NotNull(message = "ID do discente é obrigatório")
    private Long discenteId;
}
