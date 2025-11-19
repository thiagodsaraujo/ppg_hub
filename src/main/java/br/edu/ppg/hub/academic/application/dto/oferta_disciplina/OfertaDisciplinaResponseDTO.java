package br.edu.ppg.hub.academic.application.dto.oferta_disciplina;

import br.edu.ppg.hub.academic.domain.enums.StatusOferta;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta com todos os dados de uma oferta de disciplina
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfertaDisciplinaResponseDTO {

    private Long id;

    // Dados da disciplina
    private Long disciplinaId;
    private String disciplinaCodigo;
    private String disciplinaNome;
    private Integer disciplinaCreditos;

    // Dados do docente responsável
    private Long docenteResponsavelId;
    private String docenteResponsavelNome;
    private String docenteResponsavelEmail;

    // Dados do docente colaborador
    private Long docenteColaboradorId;
    private String docenteColaboradorNome;
    private String docenteColaboradorEmail;

    private Integer ano;

    private Integer semestre;

    private String periodo;

    private String turma;

    private String horarios;

    private String sala;

    private String modalidade;

    private String linkVirtual;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;

    private Integer vagasOferecidas;

    private Integer vagasOcupadas;

    private Integer listaEspera;

    private StatusOferta status;

    private String observacoes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Campos calculados
    private Integer vagasDisponiveis;
    private Double percentualOcupacao;
    private Boolean temVagasDisponiveis;
    private Boolean inscricoesAbertas;
    private Boolean ativa;
    private Boolean concluida;
    private Boolean podeCancelar;
    private Boolean permiteLancarNotas;
}
