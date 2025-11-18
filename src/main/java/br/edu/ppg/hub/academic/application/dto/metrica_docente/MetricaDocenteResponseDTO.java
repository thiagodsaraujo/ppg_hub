package br.edu.ppg.hub.academic.application.dto.metrica_docente;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta com todos os dados de uma métrica de docente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricaDocenteResponseDTO {

    private Long id;

    private Long docenteId;

    private String docenteNome;

    private Integer hIndex;

    private Integer totalPublicacoes;

    private Integer totalCitacoes;

    private Integer publicacoesUltimos5Anos;

    private String fonte;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataColeta;

    // Campos calculados
    private Double mediaCitacoesPorPublicacao;

    private Boolean temAltaProdutividade;

    private Boolean atendeMinimoCapes;
}
