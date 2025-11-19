package br.edu.ppg.hub.core.application.dto.linha_pesquisa;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de uma Linha de Pesquisa existente.
 * Todos os campos são opcionais (atualização parcial).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinhaPesquisaUpdateDTO {

    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    private String descricao;

    private String palavrasChave;

    private Long coordenadorId;

    private Boolean ativa;
}
