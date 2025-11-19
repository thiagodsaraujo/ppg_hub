package br.edu.ppg.hub.academic.application.dto.discente;

import br.edu.ppg.hub.academic.domain.enums.TipoCurso;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para matrícula/criação de um novo discente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscenteCreateDTO {

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "ID do programa é obrigatório")
    private Long programaId;

    private Long linhaPesquisaId;

    private Long orientadorId;

    @NotBlank(message = "Número de matrícula é obrigatório")
    private String numeroMatricula;

    @NotNull(message = "Tipo de curso é obrigatório")
    private TipoCurso tipoCurso;

    @NotNull(message = "Turma é obrigatória")
    private Integer turma;

    @NotBlank(message = "Semestre de ingresso é obrigatório")
    private String semestreIngresso;

    @NotNull(message = "Data de ingresso é obrigatória")
    @PastOrPresent(message = "Data de ingresso não pode ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataIngresso;

    private String tituloProjeto;

    private String resumoProjeto;

    private String palavrasChaveProjeto;

    private String areaCnpq;

    private String tipoIngresso;

    @DecimalMin(value = "0.0", message = "Nota não pode ser negativa")
    @DecimalMax(value = "100.0", message = "Nota não pode ser maior que 100")
    private BigDecimal notaProcessoSeletivo;

    @Min(value = 1, message = "Classificação deve ser maior que 0")
    private Integer classificacaoProcesso;

    // Coorientador externo
    private String coorientadorExternoNome;
    private String coorientadorExternoInstituicao;
    @Email(message = "Email do coorientador externo inválido")
    private String coorientadorExternoEmail;
    private String coorientadorExternoTitulacao;

    // Bolsa
    private Boolean bolsista;
    private String tipoBolsa;
    private String modalidadeBolsa;
    @DecimalMin(value = "0.0", message = "Valor da bolsa não pode ser negativo")
    private BigDecimal valorBolsa;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicioBolsa;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFimBolsa;
    private String numeroProcessoBolsa;
    private String agenciaFomento;

    @Min(value = 0, message = "Créditos não podem ser negativos")
    private Integer creditosNecessarios;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prazoOriginal;
}
