package br.edu.ppg.hub.academic.application.dto.docente;

import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Docente e DTOs
 *
 * @author PPG Hub
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DocenteMapper {

    /**
     * Converte CreateDTO para entidade Docente
     */
    public Docente toEntity(DocenteCreateDTO dto, Usuario usuario, Programa programa, LinhaPesquisa linhaPesquisa) {
        return Docente.builder()
                .usuario(usuario)
                .programa(programa)
                .linhaPesquisa(linhaPesquisa)
                .matricula(dto.getMatricula())
                .categoria(dto.getCategoria())
                .regimeTrabalho(dto.getRegimeTrabalho())
                .titulacaoMaxima(dto.getTitulacaoMaxima())
                .instituicaoTitulacao(dto.getInstituicaoTitulacao())
                .anoTitulacao(dto.getAnoTitulacao())
                .paisTitulacao(dto.getPaisTitulacao())
                .tipoVinculo(dto.getTipoVinculo())
                .dataVinculacao(dto.getDataVinculacao())
                .bolsistaProdutividade(dto.getBolsistaProdutividade())
                .nivelBolsaProdutividade(dto.getNivelBolsaProdutividade())
                .vigenciaBolsaInicio(dto.getVigenciaBolsaInicio())
                .vigenciaBolsaFim(dto.getVigenciaBolsaFim())
                .areasInteresse(dto.getAreasInteresse())
                .projetosAtuais(dto.getProjetosAtuais())
                .curriculoResumo(dto.getCurriculoResumo())
                .build();
    }

    /**
     * Atualiza uma entidade existente com dados do UpdateDTO
     */
    public void updateEntity(Docente docente, DocenteUpdateDTO dto, LinhaPesquisa linhaPesquisa) {
        if (dto.getLinhaPesquisaId() != null) {
            docente.setLinhaPesquisa(linhaPesquisa);
        }
        if (dto.getMatricula() != null) {
            docente.setMatricula(dto.getMatricula());
        }
        if (dto.getCategoria() != null) {
            docente.setCategoria(dto.getCategoria());
        }
        if (dto.getRegimeTrabalho() != null) {
            docente.setRegimeTrabalho(dto.getRegimeTrabalho());
        }
        if (dto.getTitulacaoMaxima() != null) {
            docente.setTitulacaoMaxima(dto.getTitulacaoMaxima());
        }
        if (dto.getInstituicaoTitulacao() != null) {
            docente.setInstituicaoTitulacao(dto.getInstituicaoTitulacao());
        }
        if (dto.getAnoTitulacao() != null) {
            docente.setAnoTitulacao(dto.getAnoTitulacao());
        }
        if (dto.getPaisTitulacao() != null) {
            docente.setPaisTitulacao(dto.getPaisTitulacao());
        }
        if (dto.getTipoVinculo() != null) {
            docente.setTipoVinculo(dto.getTipoVinculo());
        }
        if (dto.getDataVinculacao() != null) {
            docente.setDataVinculacao(dto.getDataVinculacao());
        }
        if (dto.getDataDesvinculacao() != null) {
            docente.setDataDesvinculacao(dto.getDataDesvinculacao());
        }
        if (dto.getOrientacoesMestradoAndamento() != null) {
            docente.setOrientacoesMestradoAndamento(dto.getOrientacoesMestradoAndamento());
        }
        if (dto.getOrientacoesDoutoradoAndamento() != null) {
            docente.setOrientacoesDoutoradoAndamento(dto.getOrientacoesDoutoradoAndamento());
        }
        if (dto.getOrientacoesMestradoConcluidas() != null) {
            docente.setOrientacoesMestradoConcluidas(dto.getOrientacoesMestradoConcluidas());
        }
        if (dto.getOrientacoesDoutoradoConcluidas() != null) {
            docente.setOrientacoesDoutoradoConcluidas(dto.getOrientacoesDoutoradoConcluidas());
        }
        if (dto.getCoorientacoes() != null) {
            docente.setCoorientacoes(dto.getCoorientacoes());
        }
        if (dto.getBolsistaProdutividade() != null) {
            docente.setBolsistaProdutividade(dto.getBolsistaProdutividade());
        }
        if (dto.getNivelBolsaProdutividade() != null) {
            docente.setNivelBolsaProdutividade(dto.getNivelBolsaProdutividade());
        }
        if (dto.getVigenciaBolsaInicio() != null) {
            docente.setVigenciaBolsaInicio(dto.getVigenciaBolsaInicio());
        }
        if (dto.getVigenciaBolsaFim() != null) {
            docente.setVigenciaBolsaFim(dto.getVigenciaBolsaFim());
        }
        if (dto.getAreasInteresse() != null) {
            docente.setAreasInteresse(dto.getAreasInteresse());
        }
        if (dto.getProjetosAtuais() != null) {
            docente.setProjetosAtuais(dto.getProjetosAtuais());
        }
        if (dto.getCurriculoResumo() != null) {
            docente.setCurriculoResumo(dto.getCurriculoResumo());
        }
        if (dto.getStatus() != null) {
            docente.setStatus(dto.getStatus());
        }
        if (dto.getMotivoDesligamento() != null) {
            docente.setMotivoDesligamento(dto.getMotivoDesligamento());
        }
    }

    /**
     * Converte entidade para ResponseDTO
     */
    public DocenteResponseDTO toResponseDTO(Docente docente) {
        return DocenteResponseDTO.builder()
                .id(docente.getId())
                .usuarioId(docente.getUsuario().getId())
                .usuarioNome(docente.getUsuario().getNomeCompleto())
                .usuarioEmail(docente.getUsuario().getEmail())
                .programaId(docente.getPrograma().getId())
                .programaNome(docente.getPrograma().getNome())
                .programaSigla(docente.getPrograma().getSigla())
                .linhaPesquisaId(docente.getLinhaPesquisa() != null ? docente.getLinhaPesquisa().getId() : null)
                .linhaPesquisaNome(docente.getLinhaPesquisa() != null ? docente.getLinhaPesquisa().getNome() : null)
                .matricula(docente.getMatricula())
                .categoria(docente.getCategoria())
                .regimeTrabalho(docente.getRegimeTrabalho())
                .titulacaoMaxima(docente.getTitulacaoMaxima())
                .instituicaoTitulacao(docente.getInstituicaoTitulacao())
                .anoTitulacao(docente.getAnoTitulacao())
                .paisTitulacao(docente.getPaisTitulacao())
                .tipoVinculo(docente.getTipoVinculo())
                .dataVinculacao(docente.getDataVinculacao())
                .dataDesvinculacao(docente.getDataDesvinculacao())
                .orientacoesMestradoAndamento(docente.getOrientacoesMestradoAndamento())
                .orientacoesDoutoradoAndamento(docente.getOrientacoesDoutoradoAndamento())
                .orientacoesMestradoConcluidas(docente.getOrientacoesMestradoConcluidas())
                .orientacoesDoutoradoConcluidas(docente.getOrientacoesDoutoradoConcluidas())
                .coorientacoes(docente.getCoorientacoes())
                .bolsistaProdutividade(docente.getBolsistaProdutividade())
                .nivelBolsaProdutividade(docente.getNivelBolsaProdutividade())
                .vigenciaBolsaInicio(docente.getVigenciaBolsaInicio())
                .vigenciaBolsaFim(docente.getVigenciaBolsaFim())
                .areasInteresse(docente.getAreasInteresse())
                .projetosAtuais(docente.getProjetosAtuais())
                .curriculoResumo(docente.getCurriculoResumo())
                .status(docente.getStatus())
                .motivoDesligamento(docente.getMotivoDesligamento())
                .createdAt(docente.getCreatedAt())
                .updatedAt(docente.getUpdatedAt())
                // Campos calculados
                .totalOrientacoesAndamento(docente.getTotalOrientacoesAndamento())
                .totalOrientacoesConcluidas(docente.getTotalOrientacoesConcluidas())
                .podeOrientar(docente.podeOrientar())
                .isPermanente(docente.isPermanente())
                .temBolsaVigente(docente.temBolsaProdutividadeVigente())
                .build();
    }
}
