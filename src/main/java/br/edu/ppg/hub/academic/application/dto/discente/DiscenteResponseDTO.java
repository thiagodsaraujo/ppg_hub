package br.edu.ppg.hub.academic.application.dto.discente;

import br.edu.ppg.hub.academic.domain.enums.StatusDiscente;
import br.edu.ppg.hub.academic.domain.enums.TipoCurso;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO de resposta com todos os dados de um discente
 *
 * @author PPG Hub
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscenteResponseDTO {

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

    // Dados do orientador
    private Long orientadorId;
    private String orientadorNome;

    // Dados do coorientador interno
    private Long coorientadorInternoId;
    private String coorientadorInternoNome;

    private String numeroMatricula;

    private TipoCurso tipoCurso;

    private Integer turma;

    private String semestreIngresso;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataIngresso;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPrimeiraMatricula;

    private String tituloProjeto;

    private String resumoProjeto;

    private String palavrasChaveProjeto;

    private String areaCnpq;

    private String tipoIngresso;

    private BigDecimal notaProcessoSeletivo;

    private Integer classificacaoProcesso;

    // Coorientador externo
    private String coorientadorExternoNome;
    private String coorientadorExternoInstituicao;
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
    private BigDecimal valorBolsa;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicioBolsa;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFimBolsa;
    private String numeroProcessoBolsa;
    private String agenciaFomento;

    // Desempenho
    private Integer creditosNecessarios;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Campos calculados
    private Boolean isAtivo;
    private Boolean podeDefender;
    private Boolean temOrientador;
    private Boolean temCoorientador;
    private Boolean temBolsaVigente;
    private Integer totalProrrogacoes;
    private Boolean isPrazoVencido;
    private Long mesesAtePrazo;
}
