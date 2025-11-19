package br.edu.ppg.hub.auth.application.dto.vinculacao;

import br.edu.ppg.hub.auth.domain.enums.StatusVinculacao;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de uma nova vinculação usuário-programa-role.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioProgramaRoleCreateDTO {

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "ID do programa é obrigatório")
    private Long programaId;

    @NotNull(message = "ID da role é obrigatório")
    private Long roleId;

    private LocalDate dataVinculacao;

    private LocalDate dataDesvinculacao;

    private StatusVinculacao status = StatusVinculacao.ATIVO;

    private String observacoes;
}
