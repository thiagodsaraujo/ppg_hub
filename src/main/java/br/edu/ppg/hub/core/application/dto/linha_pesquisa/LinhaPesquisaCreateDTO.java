package br.edu.ppg.hub.core.application.dto.linha_pesquisa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de uma nova Linha de Pesquisa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinhaPesquisaCreateDTO {

    @NotNull(message = "ID do programa é obrigatório")
    private Long programaId;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;

    private String descricao;

    private String palavrasChave;

    private Long coordenadorId;

    private Boolean ativa = true;
}
