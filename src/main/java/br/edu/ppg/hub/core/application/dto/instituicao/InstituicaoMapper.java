package br.edu.ppg.hub.core.application.dto.instituicao;

import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoCreateDTO;
import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoResponseDTO;
import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoUpdateDTO;
import br.edu.ppg.hub.core.domain.model.Instituicao;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mapper para conversão entre Instituicao e seus DTOs.
 *
 * Utiliza mapeamento manual para ter controle total sobre a conversão.
 */
@Component
public class InstituicaoMapper {

    /**
     * Converte CreateDTO para entidade
     */
    public Instituicao toEntity(InstituicaoCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Instituicao.builder()
                .codigo(dto.getCodigo())
                .nomeCompleto(dto.getNomeCompleto())
                .nomeAbreviado(dto.getNomeAbreviado())
                .sigla(dto.getSigla())
                .tipo(dto.getTipo())
                .cnpj(dto.getCnpj())
                .naturezaJuridica(dto.getNaturezaJuridica())
                .endereco(dto.getEndereco())
                .contatos(dto.getContatos())
                .redesSociais(dto.getRedesSociais())
                .logoUrl(dto.getLogoUrl())
                .website(dto.getWebsite())
                .fundacao(dto.getFundacao())
                .openalexInstitutionId(dto.getOpenalexInstitutionId())
                .rorId(dto.getRorId())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .configuracoes(dto.getConfiguracoes() != null ? dto.getConfiguracoes() : Map.of())
                .build();
    }

    /**
     * Converte entidade para ResponseDTO
     */
    public InstituicaoResponseDTO toResponseDTO(Instituicao entity) {
        if (entity == null) {
            return null;
        }

        return InstituicaoResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .nomeCompleto(entity.getNomeCompleto())
                .nomeAbreviado(entity.getNomeAbreviado())
                .sigla(entity.getSigla())
                .tipo(entity.getTipo())
                .cnpj(entity.getCnpj())
                .naturezaJuridica(entity.getNaturezaJuridica())
                .endereco(entity.getEndereco())
                .contatos(entity.getContatos())
                .redesSociais(entity.getRedesSociais())
                .logoUrl(entity.getLogoUrl())
                .website(entity.getWebsite())
                .fundacao(entity.getFundacao())
                .openalexInstitutionId(entity.getOpenalexInstitutionId())
                .rorId(entity.getRorId())
                .ativo(entity.getAtivo())
                .configuracoes(entity.getConfiguracoes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .enderecoCompleto(entity.getEnderecoCompleto())
                .contatoPrincipal(entity.getContatoPrincipal())
                .build();
    }

    /**
     * Atualiza entidade com dados do UpdateDTO
     * Apenas campos não-nulos são atualizados
     */
    public void updateEntityFromDTO(InstituicaoUpdateDTO dto, Instituicao entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getCodigo() != null) {
            entity.setCodigo(dto.getCodigo());
        }
        if (dto.getNomeCompleto() != null) {
            entity.setNomeCompleto(dto.getNomeCompleto());
        }
        if (dto.getNomeAbreviado() != null) {
            entity.setNomeAbreviado(dto.getNomeAbreviado());
        }
        if (dto.getSigla() != null) {
            entity.setSigla(dto.getSigla());
        }
        if (dto.getTipo() != null) {
            entity.setTipo(dto.getTipo());
        }
        if (dto.getCnpj() != null) {
            entity.setCnpj(dto.getCnpj());
        }
        if (dto.getNaturezaJuridica() != null) {
            entity.setNaturezaJuridica(dto.getNaturezaJuridica());
        }
        if (dto.getEndereco() != null) {
            entity.setEndereco(dto.getEndereco());
        }
        if (dto.getContatos() != null) {
            entity.setContatos(dto.getContatos());
        }
        if (dto.getRedesSociais() != null) {
            entity.setRedesSociais(dto.getRedesSociais());
        }
        if (dto.getLogoUrl() != null) {
            entity.setLogoUrl(dto.getLogoUrl());
        }
        if (dto.getWebsite() != null) {
            entity.setWebsite(dto.getWebsite());
        }
        if (dto.getFundacao() != null) {
            entity.setFundacao(dto.getFundacao());
        }
        if (dto.getOpenalexInstitutionId() != null) {
            entity.setOpenalexInstitutionId(dto.getOpenalexInstitutionId());
        }
        if (dto.getRorId() != null) {
            entity.setRorId(dto.getRorId());
        }
        if (dto.getAtivo() != null) {
            entity.setAtivo(dto.getAtivo());
        }
        if (dto.getConfiguracoes() != null) {
            entity.setConfiguracoes(dto.getConfiguracoes());
        }
    }
}
