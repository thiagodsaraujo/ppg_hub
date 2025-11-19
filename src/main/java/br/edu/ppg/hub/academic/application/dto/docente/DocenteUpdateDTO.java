package br.edu.ppg.hub.academic.application.dto.docente;

import br.edu.ppg.hub.academic.domain.enums.CategoriaDocente;
import br.edu.ppg.hub.academic.domain.enums.RegimeTrabalho;
import br.edu.ppg.hub.academic.domain.enums.StatusDocente;
import br.edu.ppg.hub.academic.domain.enums.TipoVinculoDocente;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para atualização de um docente existente
 * Todos os campos são opcionais
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocenteUpdateDTO {

    private Long linhaPesquisaId;

    private String matricula;

    private CategoriaDocente categoria;

    private RegimeTrabalho regimeTrabalho;

    private String titulacaoMaxima;

    private String instituicaoTitulacao;

    private Integer anoTitulacao;

    private String paisTitulacao;

    private TipoVinculoDocente tipoVinculo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVinculacao;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDesvinculacao;

    @Min(value = 0, message = "Número de orientações não pode ser negativo")
    private Integer orientacoesMestradoAndamento;

    @Min(value = 0, message = "Número de orientações não pode ser negativo")
    private Integer orientacoesDoutoradoAndamento;

    @Min(value = 0, message = "Número de orientações não pode ser negativo")
    private Integer orientacoesMestradoConcluidas;

    @Min(value = 0, message = "Número de orientações não pode ser negativo")
    private Integer orientacoesDoutoradoConcluidas;

    @Min(value = 0, message = "Número de coorientações não pode ser negativo")
    private Integer coorientacoes;

    private Boolean bolsistaProdutividade;

    private String nivelBolsaProdutividade;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vigenciaBolsaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vigenciaBolsaFim;

    private String areasInteresse;

    private String projetosAtuais;

    private String curriculoResumo;

    private StatusDocente status;

    private String motivoDesligamento;
}
