package br.edu.ppg.hub.academic.application.dto.trabalho_conclusao;

import br.edu.ppg.hub.academic.domain.model.Discente;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.TrabalhoConclusao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre TrabalhoConclusao e DTOs.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Component
@RequiredArgsConstructor
public class TrabalhoConclusaoMapper {

    /**
     * Converte CreateDTO para entidade TrabalhoConclusao.
     *
     * @param dto DTO de criação
     * @param discente Discente autor do trabalho
     * @param orientador Orientador do trabalho
     * @param coorientador Coorientador do trabalho (pode ser null)
     * @return Entidade TrabalhoConclusao
     */
    public TrabalhoConclusao toEntity(
            TrabalhoConclusaoCreateDTO dto,
            Discente discente,
            Docente orientador,
            Docente coorientador
    ) {
        return TrabalhoConclusao.builder()
                .discente(discente)
                .orientador(orientador)
                .coorientador(coorientador)
                .tipo(dto.getTipo())
                .tituloPortugues(dto.getTituloPortugues())
                .tituloIngles(dto.getTituloIngles())
                .subtitulo(dto.getSubtitulo())
                .resumoPortugues(dto.getResumoPortugues())
                .resumoIngles(dto.getResumoIngles())
                .abstractText(dto.getAbstractText())
                .palavrasChavePortugues(dto.getPalavrasChavePortugues())
                .palavrasChaveIngles(dto.getPalavrasChaveIngles())
                .keywords(dto.getKeywords())
                .areaCnpq(dto.getAreaCnpq())
                .areaConcentracao(dto.getAreaConcentracao())
                .linhaPesquisa(dto.getLinhaPesquisa())
                .dataDefesa(dto.getDataDefesa())
                .anoDefesa(dto.getAnoDefesa())
                .semestreDefesa(dto.getSemestreDefesa())
                .localDefesa(dto.getLocalDefesa())
                .numeroPaginas(dto.getNumeroPaginas())
                .idioma(dto.getIdioma() != null ? dto.getIdioma() : "pt")
                .doi(dto.getDoi())
                .isbn(dto.getIsbn())
                .premiosReconhecimentos(dto.getPremiosReconhecimentos())
                .build();
    }

    /**
     * Atualiza uma entidade existente com dados do UpdateDTO.
     *
     * @param trabalho Entidade a ser atualizada
     * @param dto DTO com dados de atualização
     * @param orientador Novo orientador (se fornecido)
     * @param coorientador Novo coorientador (se fornecido)
     */
    public void updateEntity(
            TrabalhoConclusao trabalho,
            TrabalhoConclusaoUpdateDTO dto,
            Docente orientador,
            Docente coorientador
    ) {
        if (dto.getOrientadorId() != null && orientador != null) {
            trabalho.setOrientador(orientador);
        }

        if (dto.getCoorientadorId() != null) {
            trabalho.setCoorientador(coorientador);
        }

        if (dto.getTipo() != null) {
            trabalho.setTipo(dto.getTipo());
        }

        if (dto.getTituloPortugues() != null) {
            trabalho.setTituloPortugues(dto.getTituloPortugues());
        }

        if (dto.getTituloIngles() != null) {
            trabalho.setTituloIngles(dto.getTituloIngles());
        }

        if (dto.getSubtitulo() != null) {
            trabalho.setSubtitulo(dto.getSubtitulo());
        }

        if (dto.getResumoPortugues() != null) {
            trabalho.setResumoPortugues(dto.getResumoPortugues());
        }

        if (dto.getResumoIngles() != null) {
            trabalho.setResumoIngles(dto.getResumoIngles());
        }

        if (dto.getAbstractText() != null) {
            trabalho.setAbstractText(dto.getAbstractText());
        }

        if (dto.getPalavrasChavePortugues() != null) {
            trabalho.setPalavrasChavePortugues(dto.getPalavrasChavePortugues());
        }

        if (dto.getPalavrasChaveIngles() != null) {
            trabalho.setPalavrasChaveIngles(dto.getPalavrasChaveIngles());
        }

        if (dto.getKeywords() != null) {
            trabalho.setKeywords(dto.getKeywords());
        }

        if (dto.getAreaCnpq() != null) {
            trabalho.setAreaCnpq(dto.getAreaCnpq());
        }

        if (dto.getAreaConcentracao() != null) {
            trabalho.setAreaConcentracao(dto.getAreaConcentracao());
        }

        if (dto.getLinhaPesquisa() != null) {
            trabalho.setLinhaPesquisa(dto.getLinhaPesquisa());
        }

        if (dto.getDataDefesa() != null) {
            trabalho.setDataDefesa(dto.getDataDefesa());
        }

        if (dto.getAnoDefesa() != null) {
            trabalho.setAnoDefesa(dto.getAnoDefesa());
        }

        if (dto.getSemestreDefesa() != null) {
            trabalho.setSemestreDefesa(dto.getSemestreDefesa());
        }

        if (dto.getLocalDefesa() != null) {
            trabalho.setLocalDefesa(dto.getLocalDefesa());
        }

        if (dto.getNumeroPaginas() != null) {
            trabalho.setNumeroPaginas(dto.getNumeroPaginas());
        }

        if (dto.getIdioma() != null) {
            trabalho.setIdioma(dto.getIdioma());
        }

        if (dto.getDoi() != null) {
            trabalho.setDoi(dto.getDoi());
        }

        if (dto.getIsbn() != null) {
            trabalho.setIsbn(dto.getIsbn());
        }

        if (dto.getPremiosReconhecimentos() != null) {
            trabalho.setPremiosReconhecimentos(dto.getPremiosReconhecimentos());
        }
    }

    /**
     * Converte entidade TrabalhoConclusao para ResponseDTO.
     *
     * @param trabalho Entidade a ser convertida
     * @return DTO de resposta
     */
    public TrabalhoConclusaoResponseDTO toResponseDTO(TrabalhoConclusao trabalho) {
        return TrabalhoConclusaoResponseDTO.builder()
                .id(trabalho.getId())
                .discenteId(trabalho.getDiscente().getId())
                .discenteNome(trabalho.getDiscente().getUsuario().getNomeCompleto())
                .orientadorId(trabalho.getOrientador().getId())
                .orientadorNome(trabalho.getOrientador().getNomeCompleto())
                .coorientadorId(trabalho.getCoorientador() != null ? trabalho.getCoorientador().getId() : null)
                .coorientadorNome(trabalho.getCoorientador() != null ? trabalho.getCoorientador().getNomeCompleto() : null)
                .tipo(trabalho.getTipo())
                .tituloPortugues(trabalho.getTituloPortugues())
                .tituloIngles(trabalho.getTituloIngles())
                .subtitulo(trabalho.getSubtitulo())
                .resumoPortugues(trabalho.getResumoPortugues())
                .resumoIngles(trabalho.getResumoIngles())
                .abstractText(trabalho.getAbstractText())
                .palavrasChavePortugues(trabalho.getPalavrasChavePortugues())
                .palavrasChaveIngles(trabalho.getPalavrasChaveIngles())
                .keywords(trabalho.getKeywords())
                .areaCnpq(trabalho.getAreaCnpq())
                .areaConcentracao(trabalho.getAreaConcentracao())
                .linhaPesquisa(trabalho.getLinhaPesquisa())
                .dataDefesa(trabalho.getDataDefesa())
                .anoDefesa(trabalho.getAnoDefesa())
                .semestreDefesa(trabalho.getSemestreDefesa())
                .localDefesa(trabalho.getLocalDefesa())
                .numeroPaginas(trabalho.getNumeroPaginas())
                .idioma(trabalho.getIdioma())
                .arquivoPdf(trabalho.getArquivoPdf())
                .arquivoAtaDefesa(trabalho.getArquivoAtaDefesa())
                .tamanhoArquivoBytes(trabalho.getTamanhoArquivoBytes())
                .handleUri(trabalho.getHandleUri())
                .uriRepositorio(trabalho.getUriRepositorio())
                .urlDownload(trabalho.getUrlDownload())
                .tipoAcesso(trabalho.getTipoAcesso())
                .openalexWorkId(trabalho.getOpenalexWorkId())
                .doi(trabalho.getDoi())
                .isbn(trabalho.getIsbn())
                .downloadsCount(trabalho.getDownloadsCount())
                .visualizacoesCount(trabalho.getVisualizacoesCount())
                .citacoesCount(trabalho.getCitacoesCount())
                .notaAvaliacao(trabalho.getNotaAvaliacao())
                .premiosReconhecimentos(trabalho.getPremiosReconhecimentos())
                .status(trabalho.getStatus())
                .createdAt(trabalho.getCreatedAt())
                .updatedAt(trabalho.getUpdatedAt())
                .build();
    }
}
