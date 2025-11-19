package br.edu.ppg.hub.academic.application.dto.disciplina;

import br.edu.ppg.hub.academic.domain.model.Disciplina;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Disciplina e DTOs
 *
 * @author PPG Hub
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DisciplinaMapper {

    /**
     * Converte CreateDTO para entidade Disciplina
     *
     * @param dto DTO de criação
     * @param programa Programa da disciplina
     * @param linhaPesquisa Linha de pesquisa (opcional)
     * @return Entidade Disciplina
     */
    public Disciplina toEntity(DisciplinaCreateDTO dto, Programa programa, LinhaPesquisa linhaPesquisa) {
        return Disciplina.builder()
                .programa(programa)
                .codigo(dto.getCodigo())
                .nome(dto.getNome())
                .nomeIngles(dto.getNomeIngles())
                .ementa(dto.getEmenta())
                .ementaIngles(dto.getEmentaIngles())
                .objetivos(dto.getObjetivos())
                .conteudoProgramatico(dto.getConteudoProgramatico())
                .metodologiaEnsino(dto.getMetodologiaEnsino())
                .criteriosAvaliacao(dto.getCriteriosAvaliacao())
                .bibliografiaBasica(dto.getBibliografiaBasica())
                .bibliografiaComplementar(dto.getBibliografiaComplementar())
                .cargaHorariaTotal(dto.getCargaHorariaTotal())
                .cargaHorariaTeorica(dto.getCargaHorariaTeorica() != null ? dto.getCargaHorariaTeorica() : 0)
                .cargaHorariaPratica(dto.getCargaHorariaPratica() != null ? dto.getCargaHorariaPratica() : 0)
                .creditos(dto.getCreditos())
                .tipo(dto.getTipo())
                .nivel(dto.getNivel())
                .linhaPesquisa(linhaPesquisa)
                .preRequisitos(dto.getPreRequisitos() != null ? dto.getPreRequisitos() : "[]")
                .coRequisitos(dto.getCoRequisitos() != null ? dto.getCoRequisitos() : "[]")
                .modalidade(dto.getModalidade() != null ? dto.getModalidade() : "Presencial")
                .periodicidade(dto.getPeriodicidade())
                .maximoAlunos(dto.getMaximoAlunos())
                .minimoAlunos(dto.getMinimoAlunos() != null ? dto.getMinimoAlunos() : 1)
                .build();
    }

    /**
     * Atualiza uma entidade existente com dados do UpdateDTO
     *
     * @param disciplina Entidade a ser atualizada
     * @param dto DTO com dados de atualização
     * @param linhaPesquisa Nova linha de pesquisa (opcional)
     */
    public void updateEntity(Disciplina disciplina, DisciplinaUpdateDTO dto, LinhaPesquisa linhaPesquisa) {
        if (dto.getCodigo() != null) {
            disciplina.setCodigo(dto.getCodigo());
        }
        if (dto.getNome() != null) {
            disciplina.setNome(dto.getNome());
        }
        if (dto.getNomeIngles() != null) {
            disciplina.setNomeIngles(dto.getNomeIngles());
        }
        if (dto.getEmenta() != null) {
            disciplina.setEmenta(dto.getEmenta());
        }
        if (dto.getEmentaIngles() != null) {
            disciplina.setEmentaIngles(dto.getEmentaIngles());
        }
        if (dto.getObjetivos() != null) {
            disciplina.setObjetivos(dto.getObjetivos());
        }
        if (dto.getConteudoProgramatico() != null) {
            disciplina.setConteudoProgramatico(dto.getConteudoProgramatico());
        }
        if (dto.getMetodologiaEnsino() != null) {
            disciplina.setMetodologiaEnsino(dto.getMetodologiaEnsino());
        }
        if (dto.getCriteriosAvaliacao() != null) {
            disciplina.setCriteriosAvaliacao(dto.getCriteriosAvaliacao());
        }
        if (dto.getBibliografiaBasica() != null) {
            disciplina.setBibliografiaBasica(dto.getBibliografiaBasica());
        }
        if (dto.getBibliografiaComplementar() != null) {
            disciplina.setBibliografiaComplementar(dto.getBibliografiaComplementar());
        }
        if (dto.getCargaHorariaTotal() != null) {
            disciplina.setCargaHorariaTotal(dto.getCargaHorariaTotal());
        }
        if (dto.getCargaHorariaTeorica() != null) {
            disciplina.setCargaHorariaTeorica(dto.getCargaHorariaTeorica());
        }
        if (dto.getCargaHorariaPratica() != null) {
            disciplina.setCargaHorariaPratica(dto.getCargaHorariaPratica());
        }
        if (dto.getCreditos() != null) {
            disciplina.setCreditos(dto.getCreditos());
        }
        if (dto.getTipo() != null) {
            disciplina.setTipo(dto.getTipo());
        }
        if (dto.getNivel() != null) {
            disciplina.setNivel(dto.getNivel());
        }
        if (dto.getLinhaPesquisaId() != null) {
            disciplina.setLinhaPesquisa(linhaPesquisa);
        }
        if (dto.getPreRequisitos() != null) {
            disciplina.setPreRequisitos(dto.getPreRequisitos());
        }
        if (dto.getCoRequisitos() != null) {
            disciplina.setCoRequisitos(dto.getCoRequisitos());
        }
        if (dto.getModalidade() != null) {
            disciplina.setModalidade(dto.getModalidade());
        }
        if (dto.getPeriodicidade() != null) {
            disciplina.setPeriodicidade(dto.getPeriodicidade());
        }
        if (dto.getMaximoAlunos() != null) {
            disciplina.setMaximoAlunos(dto.getMaximoAlunos());
        }
        if (dto.getMinimoAlunos() != null) {
            disciplina.setMinimoAlunos(dto.getMinimoAlunos());
        }
        if (dto.getStatus() != null) {
            disciplina.setStatus(dto.getStatus());
        }
    }

    /**
     * Converte entidade para ResponseDTO
     *
     * @param disciplina Entidade Disciplina
     * @return DTO de resposta
     */
    public DisciplinaResponseDTO toResponseDTO(Disciplina disciplina) {
        return DisciplinaResponseDTO.builder()
                .id(disciplina.getId())
                .programaId(disciplina.getPrograma().getId())
                .programaNome(disciplina.getPrograma().getNome())
                .programaSigla(disciplina.getPrograma().getSigla())
                .codigo(disciplina.getCodigo())
                .nome(disciplina.getNome())
                .nomeIngles(disciplina.getNomeIngles())
                .ementa(disciplina.getEmenta())
                .ementaIngles(disciplina.getEmentaIngles())
                .objetivos(disciplina.getObjetivos())
                .conteudoProgramatico(disciplina.getConteudoProgramatico())
                .metodologiaEnsino(disciplina.getMetodologiaEnsino())
                .criteriosAvaliacao(disciplina.getCriteriosAvaliacao())
                .bibliografiaBasica(disciplina.getBibliografiaBasica())
                .bibliografiaComplementar(disciplina.getBibliografiaComplementar())
                .cargaHorariaTotal(disciplina.getCargaHorariaTotal())
                .cargaHorariaTeorica(disciplina.getCargaHorariaTeorica())
                .cargaHorariaPratica(disciplina.getCargaHorariaPratica())
                .creditos(disciplina.getCreditos())
                .tipo(disciplina.getTipo())
                .nivel(disciplina.getNivel())
                .linhaPesquisaId(disciplina.getLinhaPesquisa() != null ? disciplina.getLinhaPesquisa().getId() : null)
                .linhaPesquisaNome(disciplina.getLinhaPesquisa() != null ? disciplina.getLinhaPesquisa().getNome() : null)
                .preRequisitos(disciplina.getPreRequisitos())
                .coRequisitos(disciplina.getCoRequisitos())
                .modalidade(disciplina.getModalidade())
                .periodicidade(disciplina.getPeriodicidade())
                .maximoAlunos(disciplina.getMaximoAlunos())
                .minimoAlunos(disciplina.getMinimoAlunos())
                .status(disciplina.getStatus())
                .createdAt(disciplina.getCreatedAt())
                .updatedAt(disciplina.getUpdatedAt())
                // Campos calculados
                .ativa(disciplina.isAtiva())
                .obrigatoria(disciplina.isObrigatoria())
                .eletiva(disciplina.isEletiva())
                .podeSerOferecida(disciplina.podeSerOferecida())
                .build();
    }
}
