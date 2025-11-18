package br.edu.ppg.hub.academic.application.dto.docente;

import br.edu.ppg.hub.academic.domain.enums.CategoriaDocente;
import br.edu.ppg.hub.academic.domain.enums.RegimeTrabalho;
import br.edu.ppg.hub.academic.domain.enums.TipoVinculoDocente;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de um novo docente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocenteCreateDTO {

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "ID do programa é obrigatório")
    private Long programaId;

    private Long linhaPesquisaId;

    private String matricula;

    private CategoriaDocente categoria;

    private RegimeTrabalho regimeTrabalho;

    private String titulacaoMaxima;

    private String instituicaoTitulacao;

    private Integer anoTitulacao;

    private String paisTitulacao;

    @NotNull(message = "Tipo de vínculo é obrigatório")
    private TipoVinculoDocente tipoVinculo;

    @NotNull(message = "Data de vinculação é obrigatória")
    @PastOrPresent(message = "Data de vinculação não pode ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVinculacao;

    private Boolean bolsistaProdutividade;

    private String nivelBolsaProdutividade;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vigenciaBolsaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vigenciaBolsaFim;

    private String areasInteresse;

    private String projetosAtuais;

    private String curriculoResumo;
}
