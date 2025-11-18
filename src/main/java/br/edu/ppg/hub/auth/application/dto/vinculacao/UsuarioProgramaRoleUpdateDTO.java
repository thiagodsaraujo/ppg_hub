package br.edu.ppg.hub.auth.application.dto.vinculacao;

import br.edu.ppg.hub.auth.domain.enums.StatusVinculacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para atualização de uma vinculação usuário-programa-role.
 * Todos os campos são opcionais (atualização parcial).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioProgramaRoleUpdateDTO {

    private LocalDate dataDesvinculacao;

    private StatusVinculacao status;

    private String observacoes;
}
