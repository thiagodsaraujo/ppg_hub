package br.edu.ppg.hub.auth.application.dto.vinculacao;

import br.edu.ppg.hub.auth.domain.enums.StatusVinculacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta com dados de uma vinculação usuário-programa-role.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioProgramaRoleResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private Long programaId;
    private String programaNome;
    private String programaSigla;
    private Long roleId;
    private String roleNome;
    private String roleDescricao;
    private LocalDate dataVinculacao;
    private LocalDate dataDesvinculacao;
    private StatusVinculacao status;
    private String observacoes;
    private LocalDateTime createdAt;
    private Long createdById;
    private String createdByNome;

    // Campos calculados
    private Boolean ativa;
    private Boolean vigente;
}
