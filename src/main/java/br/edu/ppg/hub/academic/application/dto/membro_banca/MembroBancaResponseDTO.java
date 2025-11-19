package br.edu.ppg.hub.academic.application.dto.membro_banca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta para um membro de banca.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembroBancaResponseDTO {

    private Long id;

    // Relacionamentos
    private Long bancaId;

    private Long docenteId;
    private String docenteNome;

    // Dados do membro
    private String nomeCompleto;
    private String instituicao;
    private String titulacao;
    private String email;
    private String curriculoResumo;

    // Função e tipo
    private String funcao;
    private String tipo;
    private Integer ordemApresentacao;

    // Participação
    private Boolean confirmado;
    private Boolean presente;
    private String justificativaAusencia;

    // Avaliação
    private String parecerIndividual;
    private BigDecimal notaIndividual;
    private String arquivoParecer;

    // Auditoria
    private LocalDateTime createdAt;
}
