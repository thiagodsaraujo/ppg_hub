package br.edu.ppg.hub.academic.application.dto.oferta_disciplina;

import br.edu.ppg.hub.academic.domain.enums.StatusOferta;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para atualização de uma oferta de disciplina existente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfertaDisciplinaUpdateDTO {

    private Long docenteResponsavelId;

    private Long docenteColaboradorId;

    private String horarios;

    @Size(max = 50, message = "Sala deve ter no máximo 50 caracteres")
    private String sala;

    @Pattern(regexp = "Presencial|EAD|Híbrida", message = "Modalidade deve ser Presencial, EAD ou Híbrida")
    private String modalidade;

    private String linkVirtual;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;

    @Min(value = 1, message = "Deve haver pelo menos 1 vaga")
    @Max(value = 100, message = "Máximo de 100 vagas por turma")
    private Integer vagasOferecidas;

    private StatusOferta status;

    private String observacoes;
}
