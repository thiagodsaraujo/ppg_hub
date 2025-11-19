package br.edu.ppg.hub.academic.application.dto.disciplina;

import br.edu.ppg.hub.academic.domain.enums.StatusDisciplina;
import br.edu.ppg.hub.academic.domain.enums.TipoDisciplina;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de uma disciplina existente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinaUpdateDTO {

    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String codigo;

    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    @Size(max = 255, message = "Nome em inglês deve ter no máximo 255 caracteres")
    private String nomeIngles;

    private String ementa;

    private String ementaIngles;

    private String objetivos;

    private String conteudoProgramatico;

    private String metodologiaEnsino;

    private String criteriosAvaliacao;

    private String bibliografiaBasica;

    private String bibliografiaComplementar;

    @Min(value = 1, message = "Carga horária total deve ser maior que zero")
    private Integer cargaHorariaTotal;

    @Min(value = 0, message = "Carga horária teórica não pode ser negativa")
    private Integer cargaHorariaTeorica;

    @Min(value = 0, message = "Carga horária prática não pode ser negativa")
    private Integer cargaHorariaPratica;

    @Min(value = 1, message = "Créditos devem ser maior que zero")
    private Integer creditos;

    private TipoDisciplina tipo;

    @Pattern(regexp = "Mestrado|Doutorado|Ambos", message = "Nível deve ser Mestrado, Doutorado ou Ambos")
    private String nivel;

    private Long linhaPesquisaId;

    private String preRequisitos;

    private String coRequisitos;

    @Pattern(regexp = "Presencial|EAD|Híbrida", message = "Modalidade deve ser Presencial, EAD ou Híbrida")
    private String modalidade;

    @Pattern(regexp = "Anual|Semestral|Eventual", message = "Periodicidade deve ser Anual, Semestral ou Eventual")
    private String periodicidade;

    @Min(value = 1, message = "Máximo de alunos deve ser pelo menos 1")
    private Integer maximoAlunos;

    @Min(value = 1, message = "Mínimo de alunos deve ser pelo menos 1")
    private Integer minimoAlunos;

    private StatusDisciplina status;
}
