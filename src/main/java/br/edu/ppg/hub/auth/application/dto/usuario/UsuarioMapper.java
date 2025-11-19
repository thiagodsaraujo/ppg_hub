package br.edu.ppg.hub.auth.application.dto.usuario;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Mapper manual para conversão entre Usuario e seus DTOs.
 *
 * Alternativa: Usar MapStruct para geração automática.
 */
@Component
public class UsuarioMapper {

    /**
     * Converte UsuarioCreateDTO para entidade Usuario.
     *
     * @param dto DTO de criação
     * @return Entidade Usuario
     */
    public Usuario toEntity(UsuarioCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Usuario.builder()
                .nomeCompleto(dto.getNomeCompleto())
                .nomePreferido(dto.getNomePreferido())
                .email(dto.getEmail())
                .emailAlternativo(dto.getEmailAlternativo())
                .telefone(dto.getTelefone())
                .cpf(dto.getCpf())
                .rg(dto.getRg())
                .passaporte(dto.getPassaporte())
                .passwordHash(dto.getPassword()) // Será encriptado no service
                .dataNascimento(dto.getDataNascimento())
                .genero(dto.getGenero())
                .nacionalidade(dto.getNacionalidade())
                .naturalidade(dto.getNaturalidade())
                .endereco(dto.getEndereco())
                .orcid(dto.getOrcid())
                .lattesId(dto.getLattesId())
                .googleScholarId(dto.getGoogleScholarId())
                .researchgateId(dto.getResearchgateId())
                .linkedin(dto.getLinkedin())
                .avatarUrl(dto.getAvatarUrl())
                .biografia(dto.getBiografia())
                .emailVerificado(false)
                .ativo(true)
                .tentativasLogin(0)
                .contaBloqueada(false)
                .build();
    }

    /**
     * Converte entidade Usuario para UsuarioResponseDTO.
     *
     * @param usuario Entidade Usuario
     * @return DTO de resposta
     */
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .uuid(usuario.getUuid())
                .nomeCompleto(usuario.getNomeCompleto())
                .nomePreferido(usuario.getNomePreferido())
                .email(usuario.getEmail())
                .emailAlternativo(usuario.getEmailAlternativo())
                .telefone(usuario.getTelefone())
                .cpf(usuario.getCpf())
                .rg(usuario.getRg())
                .passaporte(usuario.getPassaporte())
                .emailVerificado(usuario.getEmailVerificado())
                .emailVerificadoEm(usuario.getEmailVerificadoEm())
                .dataNascimento(usuario.getDataNascimento())
                .genero(usuario.getGenero())
                .nacionalidade(usuario.getNacionalidade())
                .naturalidade(usuario.getNaturalidade())
                .endereco(usuario.getEndereco())
                .orcid(usuario.getOrcid())
                .lattesId(usuario.getLattesId())
                .googleScholarId(usuario.getGoogleScholarId())
                .researchgateId(usuario.getResearchgateId())
                .linkedin(usuario.getLinkedin())
                .openalexAuthorId(usuario.getOpenalexAuthorId())
                .ultimoSyncOpenalex(usuario.getUltimoSyncOpenalex())
                .configuracoes(usuario.getConfiguracoes())
                .preferencias(usuario.getPreferencias())
                .avatarUrl(usuario.getAvatarUrl())
                .biografia(usuario.getBiografia())
                .ultimoLogin(usuario.getUltimoLogin())
                .contaBloqueada(usuario.getContaBloqueada())
                .bloqueadaAte(usuario.getBloqueadaAte())
                .ativo(usuario.getAtivo())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .roles(Collections.emptyList()) // TODO: Adicionar roles quando implementar relacionamento
                .build();
    }

    /**
     * Atualiza uma entidade Usuario existente com dados do UsuarioUpdateDTO.
     *
     * @param usuario Entidade existente
     * @param dto DTO com dados para atualização
     */
    public void updateEntityFromDTO(Usuario usuario, UsuarioUpdateDTO dto) {
        if (usuario == null || dto == null) {
            return;
        }

        if (dto.getNomeCompleto() != null) {
            usuario.setNomeCompleto(dto.getNomeCompleto());
        }
        if (dto.getNomePreferido() != null) {
            usuario.setNomePreferido(dto.getNomePreferido());
        }
        if (dto.getEmail() != null) {
            usuario.setEmail(dto.getEmail());
        }
        if (dto.getEmailAlternativo() != null) {
            usuario.setEmailAlternativo(dto.getEmailAlternativo());
        }
        if (dto.getTelefone() != null) {
            usuario.setTelefone(dto.getTelefone());
        }
        if (dto.getRg() != null) {
            usuario.setRg(dto.getRg());
        }
        if (dto.getDataNascimento() != null) {
            usuario.setDataNascimento(dto.getDataNascimento());
        }
        if (dto.getGenero() != null) {
            usuario.setGenero(dto.getGenero());
        }
        if (dto.getNacionalidade() != null) {
            usuario.setNacionalidade(dto.getNacionalidade());
        }
        if (dto.getNaturalidade() != null) {
            usuario.setNaturalidade(dto.getNaturalidade());
        }
        if (dto.getEndereco() != null) {
            usuario.setEndereco(dto.getEndereco());
        }
        if (dto.getOrcid() != null) {
            usuario.setOrcid(dto.getOrcid());
        }
        if (dto.getLattesId() != null) {
            usuario.setLattesId(dto.getLattesId());
        }
        if (dto.getGoogleScholarId() != null) {
            usuario.setGoogleScholarId(dto.getGoogleScholarId());
        }
        if (dto.getResearchgateId() != null) {
            usuario.setResearchgateId(dto.getResearchgateId());
        }
        if (dto.getLinkedin() != null) {
            usuario.setLinkedin(dto.getLinkedin());
        }
        if (dto.getAvatarUrl() != null) {
            usuario.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getBiografia() != null) {
            usuario.setBiografia(dto.getBiografia());
        }
        if (dto.getConfiguracoes() != null) {
            usuario.setConfiguracoes(dto.getConfiguracoes());
        }
        if (dto.getPreferencias() != null) {
            usuario.setPreferencias(dto.getPreferencias());
        }
    }
}
