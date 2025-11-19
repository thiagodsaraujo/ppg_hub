package br.edu.ppg.hub.academic.application.dto.disciplina;

import br.edu.ppg.hub.academic.domain.enums.TipoDisciplina;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de uma nova disciplina
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinaCreateDTO {

    @NotNull(message = "ID do programa é obrigatório")
    private Long programaId;

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String codigo;

    @NotBlank(message = "Nome é obrigatório")
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

    @NotNull(message = "Carga horária total é obrigatória")
    @Min(value = 1, message = "Carga horária total deve ser maior que zero")
    private Integer cargaHorariaTotal;

    @Min(value = 0, message = "Carga horária teórica não pode ser negativa")
    private Integer cargaHorariaTeorica;

    @Min(value = 0, message = "Carga horária prática não pode ser negativa")
    private Integer cargaHorariaPratica;

    @NotNull(message = "Créditos são obrigatórios")
    @Min(value = 1, message = "Créditos devem ser maior que zero")
    private Integer creditos;

    @NotNull(message = "Tipo é obrigatório")
    private TipoDisciplina tipo;

    @NotBlank(message = "Nível é obrigatório")
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
}
