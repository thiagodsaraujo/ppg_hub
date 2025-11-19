package br.edu.ppg.hub.academic.application.dto.metrica_docente;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de uma nova métrica de docente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricaDocenteCreateDTO {

    @NotNull(message = "ID do docente é obrigatório")
    private Long docenteId;

    @Min(value = 0, message = "H-index não pode ser negativo")
    private Integer hIndex;

    @Min(value = 0, message = "Total de publicações não pode ser negativo")
    private Integer totalPublicacoes;

    @Min(value = 0, message = "Total de citações não pode ser negativo")
    private Integer totalCitacoes;

    @Min(value = 0, message = "Publicações não pode ser negativo")
    private Integer publicacoesUltimos5Anos;

    private String fonte;
}
