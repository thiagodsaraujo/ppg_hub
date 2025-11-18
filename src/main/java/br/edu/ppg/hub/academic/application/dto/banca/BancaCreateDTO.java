package br.edu.ppg.hub.academic.application.dto.banca;

import br.edu.ppg.hub.academic.domain.enums.TipoBanca;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * DTO para criação de uma banca examinadora.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BancaCreateDTO {

    private Long trabalhoConclusaoId;

    @NotNull(message = "O ID do discente é obrigatório")
    private Long discenteId;

    @NotNull(message = "O tipo da banca é obrigatório")
    private TipoBanca tipo;

    @NotNull(message = "A data agendada é obrigatória")
    @Future(message = "A data agendada deve ser futura")
    private LocalDate dataAgendada;

    @NotNull(message = "O horário de início é obrigatório")
    private LocalTime horarioInicio;

    private LocalTime horarioFim;

    @Size(max = 500, message = "O local de realização não pode exceder 500 caracteres")
    private String localRealizacao;

    @Pattern(regexp = "^(Presencial|Virtual|Híbrida)$", message = "Modalidade inválida")
    private String modalidade;

    private String linkVirtual;

    @NotNull(message = "O ID do presidente é obrigatório")
    private Long presidenteId;

    private Long secretarioId;

    private Map<String, Object> pauta;
}
