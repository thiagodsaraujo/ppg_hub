package br.edu.ppg.hub.academic.application.dto.docente;

import br.edu.ppg.hub.academic.domain.enums.CategoriaDocente;
import br.edu.ppg.hub.academic.domain.enums.RegimeTrabalho;
import br.edu.ppg.hub.academic.domain.enums.StatusDocente;
import br.edu.ppg.hub.academic.domain.enums.TipoVinculoDocente;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta com todos os dados de um docente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocenteResponseDTO {

    private Long id;

    // Dados do usuário
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;

    // Dados do programa
    private Long programaId;
    private String programaNome;
    private String programaSigla;

    // Dados da linha de pesquisa
    private Long linhaPesquisaId;
    private String linhaPesquisaNome;

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

    private Integer orientacoesMestradoAndamento;

    private Integer orientacoesDoutoradoAndamento;

    private Integer orientacoesMestradoConcluidas;

    private Integer orientacoesDoutoradoConcluidas;

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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Campos calculados
    private Integer totalOrientacoesAndamento;
    private Integer totalOrientacoesConcluidas;
    private Boolean podeOrientar;
    private Boolean isPermanente;
    private Boolean temBolsaVigente;
}
