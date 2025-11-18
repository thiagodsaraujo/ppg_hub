package br.edu.ppg.hub.auth.application.dto.vinculacao;

import br.edu.ppg.hub.auth.domain.model.Role;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.auth.domain.model.UsuarioProgramaRole;
import br.edu.ppg.hub.core.domain.model.Programa;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre UsuarioProgramaRole e seus DTOs.
 */
@Component
public class UsuarioProgramaRoleMapper {

    /**
     * Converte DTO de criação para entidade UsuarioProgramaRole.
     */
    public UsuarioProgramaRole toEntity(UsuarioProgramaRoleCreateDTO dto, Usuario usuario, Programa programa, Role role, Usuario createdBy) {
        return UsuarioProgramaRole.builder()
                .usuario(usuario)
                .programa(programa)
                .role(role)
                .dataVinculacao(dto.getDataVinculacao())
                .dataDesvinculacao(dto.getDataDesvinculacao())
                .status(dto.getStatus())
                .observacoes(dto.getObservacoes())
                .createdBy(createdBy)
                .build();
    }

    /**
     * Atualiza entidade UsuarioProgramaRole com dados do DTO de atualização.
     */
    public void updateEntity(UsuarioProgramaRole vinculacao, UsuarioProgramaRoleUpdateDTO dto) {
        if (dto.getDataDesvinculacao() != null) {
            vinculacao.setDataDesvinculacao(dto.getDataDesvinculacao());
        }
        if (dto.getStatus() != null) {
            vinculacao.setStatus(dto.getStatus());
        }
        if (dto.getObservacoes() != null) {
            vinculacao.setObservacoes(dto.getObservacoes());
        }
    }

    /**
     * Converte entidade UsuarioProgramaRole para DTO de resposta.
     */
    public UsuarioProgramaRoleResponseDTO toResponseDTO(UsuarioProgramaRole vinculacao) {
        return UsuarioProgramaRoleResponseDTO.builder()
                .id(vinculacao.getId())
                .usuarioId(vinculacao.getUsuario().getId())
                .usuarioNome(vinculacao.getUsuario().getNomeCompleto())
                .usuarioEmail(vinculacao.getUsuario().getEmail())
                .programaId(vinculacao.getPrograma().getId())
                .programaNome(vinculacao.getPrograma().getNome())
                .programaSigla(vinculacao.getPrograma().getSigla())
                .roleId(vinculacao.getRole().getId())
                .roleNome(vinculacao.getRole().getNome())
                .roleDescricao(vinculacao.getRole().getDescricao())
                .dataVinculacao(vinculacao.getDataVinculacao())
                .dataDesvinculacao(vinculacao.getDataDesvinculacao())
                .status(vinculacao.getStatus())
                .observacoes(vinculacao.getObservacoes())
                .createdAt(vinculacao.getCreatedAt())
                .createdById(vinculacao.getCreatedBy() != null ? vinculacao.getCreatedBy().getId() : null)
                .createdByNome(vinculacao.getCreatedBy() != null ? vinculacao.getCreatedBy().getNomeCompleto() : null)
                .ativa(vinculacao.isAtiva())
                .vigente(vinculacao.isVigente())
                .build();
    }
}
