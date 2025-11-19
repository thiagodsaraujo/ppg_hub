package br.edu.ppg.hub.academic.application.dto.oferta_disciplina;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de uma nova oferta de disciplina
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfertaDisciplinaCreateDTO {

    @NotNull(message = "ID da disciplina é obrigatório")
    private Long disciplinaId;

    @NotNull(message = "ID do docente responsável é obrigatório")
    private Long docenteResponsavelId;

    private Long docenteColaboradorId;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2020, message = "Ano deve ser maior ou igual a 2020")
    @Max(value = 2100, message = "Ano deve ser menor ou igual a 2100")
    private Integer ano;

    @NotNull(message = "Semestre é obrigatório")
    @Min(value = 1, message = "Semestre deve ser 1 ou 2")
    @Max(value = 2, message = "Semestre deve ser 1 ou 2")
    private Integer semestre;

    @NotBlank(message = "Período é obrigatório")
    @Size(max = 10, message = "Período deve ter no máximo 10 caracteres")
    @Pattern(regexp = "\\d{4}\\.[12]", message = "Período deve estar no formato AAAA.S (ex: 2024.1)")
    private String periodo;

    @Size(max = 10, message = "Turma deve ter no máximo 10 caracteres")
    private String turma;

    @NotNull(message = "Horários são obrigatórios")
    private String horarios;

    @Size(max = 50, message = "Sala deve ter no máximo 50 caracteres")
    private String sala;

    @Pattern(regexp = "Presencial|EAD|Híbrida", message = "Modalidade deve ser Presencial, EAD ou Híbrida")
    private String modalidade;

    private String linkVirtual;

    @NotNull(message = "Data de início é obrigatória")
    @Future(message = "Data de início deve ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    @Future(message = "Data de fim deve ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;

    @NotNull(message = "Vagas oferecidas são obrigatórias")
    @Min(value = 1, message = "Deve haver pelo menos 1 vaga")
    @Max(value = 100, message = "Máximo de 100 vagas por turma")
    private Integer vagasOferecidas;

    private String observacoes;
}
