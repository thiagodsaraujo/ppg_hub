package br.edu.ppg.hub.core.application.dto.linha_pesquisa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta com dados de uma Linha de Pesquisa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinhaPesquisaResponseDTO {

    private Long id;
    private Long programaId;
    private String programaNome;
    private String programaSigla;
    private String nome;
    private String descricao;
    private String palavrasChave;
    private Long coordenadorId;
    private String coordenadorNome;
    private Boolean ativa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
