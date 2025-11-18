package br.edu.ppg.hub.core.application.dto.programa;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.core.application.dto.instituicao.InstituicaoMapper;
import br.edu.ppg.hub.core.domain.model.Instituicao;
import br.edu.ppg.hub.core.domain.model.Programa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Programa e seus DTOs.
 */
@Component
@RequiredArgsConstructor
public class ProgramaMapper {

    private final InstituicaoMapper instituicaoMapper;

    /**
     * Converte DTO de criação para entidade Programa.
     */
    public Programa toEntity(ProgramaCreateDTO dto, Instituicao instituicao, Usuario coordenador, Usuario coordenadorAdjunto) {
        return Programa.builder()
                .instituicao(instituicao)
                .codigoCapes(dto.getCodigoCapes())
                .nome(dto.getNome())
                .sigla(dto.getSigla())
                .areaConcentracao(dto.getAreaConcentracao())
                .nivel(dto.getNivel())
                .modalidade(dto.getModalidade())
                .inicioFuncionamento(dto.getInicioFuncionamento())
                .conceitoCapes(dto.getConceitoCapes())
                .dataUltimaAvaliacao(dto.getDataUltimaAvaliacao())
                .trienioAvaliacao(dto.getTrienioAvaliacao())
                .coordenador(coordenador)
                .coordenadorAdjunto(coordenadorAdjunto)
                .mandatoInicio(dto.getMandatoInicio())
                .mandatoFim(dto.getMandatoFim())
                .creditosMinimosMestrado(dto.getCreditosMinimosMestrado())
                .creditosMinimosDoutorado(dto.getCreditosMinimosDoutorado())
                .prazoMaximoMestrado(dto.getPrazoMaximoMestrado())
                .prazoMaximoDoutorado(dto.getPrazoMaximoDoutorado())
                .openalexInstitutionId(dto.getOpenalexInstitutionId())
                .status(dto.getStatus())
                .configuracoes(dto.getConfiguracoes() != null ? dto.getConfiguracoes() : "{}")
                .build();
    }

    /**
     * Atualiza entidade Programa com dados do DTO de atualização.
     */
    public void updateEntity(Programa programa, ProgramaUpdateDTO dto, Usuario coordenador, Usuario coordenadorAdjunto) {
        if (dto.getCodigoCapes() != null) {
            programa.setCodigoCapes(dto.getCodigoCapes());
        }
        if (dto.getNome() != null) {
            programa.setNome(dto.getNome());
        }
        if (dto.getSigla() != null) {
            programa.setSigla(dto.getSigla());
        }
        if (dto.getAreaConcentracao() != null) {
            programa.setAreaConcentracao(dto.getAreaConcentracao());
        }
        if (dto.getNivel() != null) {
            programa.setNivel(dto.getNivel());
        }
        if (dto.getModalidade() != null) {
            programa.setModalidade(dto.getModalidade());
        }
        if (dto.getInicioFuncionamento() != null) {
            programa.setInicioFuncionamento(dto.getInicioFuncionamento());
        }
        if (dto.getConceitoCapes() != null) {
            programa.setConceitoCapes(dto.getConceitoCapes());
        }
        if (dto.getDataUltimaAvaliacao() != null) {
            programa.setDataUltimaAvaliacao(dto.getDataUltimaAvaliacao());
        }
        if (dto.getTrienioAvaliacao() != null) {
            programa.setTrienioAvaliacao(dto.getTrienioAvaliacao());
        }
        if (coordenador != null) {
            programa.setCoordenador(coordenador);
        }
        if (coordenadorAdjunto != null) {
            programa.setCoordenadorAdjunto(coordenadorAdjunto);
        }
        if (dto.getMandatoInicio() != null) {
            programa.setMandatoInicio(dto.getMandatoInicio());
        }
        if (dto.getMandatoFim() != null) {
            programa.setMandatoFim(dto.getMandatoFim());
        }
        if (dto.getCreditosMinimosMestrado() != null) {
            programa.setCreditosMinimosMestrado(dto.getCreditosMinimosMestrado());
        }
        if (dto.getCreditosMinimosDoutorado() != null) {
            programa.setCreditosMinimosDoutorado(dto.getCreditosMinimosDoutorado());
        }
        if (dto.getPrazoMaximoMestrado() != null) {
            programa.setPrazoMaximoMestrado(dto.getPrazoMaximoMestrado());
        }
        if (dto.getPrazoMaximoDoutorado() != null) {
            programa.setPrazoMaximoDoutorado(dto.getPrazoMaximoDoutorado());
        }
        if (dto.getOpenalexInstitutionId() != null) {
            programa.setOpenalexInstitutionId(dto.getOpenalexInstitutionId());
        }
        if (dto.getStatus() != null) {
            programa.setStatus(dto.getStatus());
        }
        if (dto.getConfiguracoes() != null) {
            programa.setConfiguracoes(dto.getConfiguracoes());
        }
    }

    /**
     * Converte entidade Programa para DTO de resposta.
     */
    public ProgramaResponseDTO toResponseDTO(Programa programa) {
        return ProgramaResponseDTO.builder()
                .id(programa.getId())
                .instituicao(instituicaoMapper.toResponseDTO(programa.getInstituicao()))
                .codigoCapes(programa.getCodigoCapes())
                .nome(programa.getNome())
                .sigla(programa.getSigla())
                .areaConcentracao(programa.getAreaConcentracao())
                .nivel(programa.getNivel())
                .modalidade(programa.getModalidade())
                .inicioFuncionamento(programa.getInicioFuncionamento())
                .conceitoCapes(programa.getConceitoCapes())
                .dataUltimaAvaliacao(programa.getDataUltimaAvaliacao())
                .trienioAvaliacao(programa.getTrienioAvaliacao())
                .coordenadorId(programa.getCoordenador() != null ? programa.getCoordenador().getId() : null)
                .coordenadorNome(programa.getCoordenador() != null ? programa.getCoordenador().getNomeCompleto() : null)
                .coordenadorAdjuntoId(programa.getCoordenadorAdjunto() != null ? programa.getCoordenadorAdjunto().getId() : null)
                .coordenadorAdjuntoNome(programa.getCoordenadorAdjunto() != null ? programa.getCoordenadorAdjunto().getNomeCompleto() : null)
                .mandatoInicio(programa.getMandatoInicio())
                .mandatoFim(programa.getMandatoFim())
                .creditosMinimosMestrado(programa.getCreditosMinimosMestrado())
                .creditosMinimosDoutorado(programa.getCreditosMinimosDoutorado())
                .prazoMaximoMestrado(programa.getPrazoMaximoMestrado())
                .prazoMaximoDoutorado(programa.getPrazoMaximoDoutorado())
                .openalexInstitutionId(programa.getOpenalexInstitutionId())
                .status(programa.getStatus())
                .configuracoes(programa.getConfiguracoes())
                .createdAt(programa.getCreatedAt())
                .updatedAt(programa.getUpdatedAt())
                .ativo(programa.isAtivo())
                .ofereceMestrado(programa.ofereceMestrado())
                .ofereceDoutorado(programa.ofereceDoutorado())
                .mandatoVigente(programa.mandatoVigente())
                .build();
    }
}
