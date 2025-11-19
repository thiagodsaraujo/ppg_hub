package br.edu.ppg.hub.academic.application.dto.banca;

import br.edu.ppg.hub.academic.domain.enums.ResultadoBanca;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * DTO para atualização de uma banca examinadora.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BancaUpdateDTO {

    private LocalDate dataAgendada;

    private LocalTime horarioInicio;

    private LocalTime horarioFim;

    @Size(max = 500, message = "O local de realização não pode exceder 500 caracteres")
    private String localRealizacao;

    @Pattern(regexp = "^(Presencial|Virtual|Híbrida)$", message = "Modalidade inválida")
    private String modalidade;

    private String linkVirtual;

    private Long presidenteId;

    private Long secretarioId;

    @Size(max = 50, message = "O número da ata não pode exceder 50 caracteres")
    private String ataNumero;

    private Map<String, Object> pauta;

    private Map<String, Object> ata;

    private ResultadoBanca resultado;

    @DecimalMin(value = "0.00", message = "A nota deve ser maior ou igual a 0")
    @DecimalMax(value = "10.00", message = "A nota deve ser menor ou igual a 10")
    private BigDecimal notaFinal;

    @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia")
    @Max(value = 365, message = "O prazo não pode exceder 365 dias")
    private Integer prazoCorrecoesDias;

    @Size(max = 5000, message = "As correções exigidas não podem exceder 5000 caracteres")
    private String correcoesExigidas;

    @Size(max = 5000, message = "As observações não podem exceder 5000 caracteres")
    private String observacoesBanca;

    @Size(max = 5000, message = "As recomendações não podem exceder 5000 caracteres")
    private String recomendacoes;

    private Boolean sugestaoPublicacao;
}
