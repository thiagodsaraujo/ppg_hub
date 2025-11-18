package br.edu.ppg.hub.academic.application.dto.disciplina;

import br.edu.ppg.hub.academic.domain.enums.StatusDisciplina;
import br.edu.ppg.hub.academic.domain.enums.TipoDisciplina;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta com todos os dados de uma disciplina
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DisciplinaResponseDTO {

    private Long id;

    // Dados do programa
    private Long programaId;
    private String programaNome;
    private String programaSigla;

    private String codigo;

    private String nome;

    private String nomeIngles;

    private String ementa;

    private String ementaIngles;

    private String objetivos;

    private String conteudoProgramatico;

    private String metodologiaEnsino;

    private String criteriosAvaliacao;

    private String bibliografiaBasica;

    private String bibliografiaComplementar;

    private Integer cargaHorariaTotal;

    private Integer cargaHorariaTeorica;

    private Integer cargaHorariaPratica;

    private Integer creditos;

    private TipoDisciplina tipo;

    private String nivel;

    // Dados da linha de pesquisa
    private Long linhaPesquisaId;
    private String linhaPesquisaNome;

    private String preRequisitos;

    private String coRequisitos;

    private String modalidade;

    private String periodicidade;

    private Integer maximoAlunos;

    private Integer minimoAlunos;

    private StatusDisciplina status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Campos calculados
    private Boolean ativa;
    private Boolean obrigatoria;
    private Boolean eletiva;
    private Boolean podeSerOferecida;
}
