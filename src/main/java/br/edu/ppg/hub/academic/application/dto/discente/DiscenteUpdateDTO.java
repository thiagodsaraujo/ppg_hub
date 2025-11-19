package br.edu.ppg.hub.academic.application.dto.discente;

import br.edu.ppg.hub.academic.domain.enums.StatusDiscente;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO para atualização de um discente existente
 * Todos os campos são opcionais
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscenteUpdateDTO {

    private Long linhaPesquisaId;

    private Long orientadorId;

    private Long coorientadorInternoId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPrimeiraMatricula;

    private String tituloProjeto;

    private String resumoProjeto;

    private String palavrasChaveProjeto;

    private String areaCnpq;

    // Coorientador externo
    private String coorientadorExternoNome;
    private String coorientadorExternoInstituicao;
    @Email(message = "Email do coorientador externo inválido")
    private String coorientadorExternoEmail;
    private String coorientadorExternoTitulacao;

    // Proficiência
    private String proficienciaIdioma;
    private String proficienciaStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataProficiencia;
    private String arquivoProficiencia;

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

    // Desempenho
    @Min(value = 0, message = "Créditos não podem ser negativos")
    private Integer creditosNecessarios;
    @DecimalMin(value = "0.0", message = "Coeficiente não pode ser negativo")
    @DecimalMax(value = "10.0", message = "Coeficiente não pode ser maior que 10")
    private BigDecimal coeficienteRendimento;

    // Qualificação
    private Boolean qualificacaoRealizada;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataQualificacao;
    private String resultadoQualificacao;

    // Prazos
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prazoOriginal;
    private List<Map<String, Object>> prorrogacoes;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataLimiteAtual;

    // Defesa
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDefesa;
    private String resultadoDefesa;
    @DecimalMin(value = "0.0", message = "Nota não pode ser negativa")
    @DecimalMax(value = "10.0", message = "Nota não pode ser maior que 10")
    private BigDecimal notaDefesa;
    private String tituloFinal;

    private Map<String, String> documentos;

    private StatusDiscente status;

    private String motivoDesligamento;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDesligamento;

    // Egresso
    private String destinoEgresso;
    private String atuacaoPosFormatura;
}
