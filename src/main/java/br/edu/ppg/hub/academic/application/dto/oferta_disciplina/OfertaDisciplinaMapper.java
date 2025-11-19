package br.edu.ppg.hub.academic.application.dto.oferta_disciplina;

import br.edu.ppg.hub.academic.domain.model.Disciplina;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.OfertaDisciplina;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre OfertaDisciplina e DTOs
 *
 * @author PPG Hub
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class OfertaDisciplinaMapper {

    /**
     * Converte CreateDTO para entidade OfertaDisciplina
     *
     * @param dto DTO de criação
     * @param disciplina Disciplina sendo ofertada
     * @param docenteResponsavel Docente responsável
     * @param docenteColaborador Docente colaborador (opcional)
     * @return Entidade OfertaDisciplina
     */
    public OfertaDisciplina toEntity(
            OfertaDisciplinaCreateDTO dto,
            Disciplina disciplina,
            Docente docenteResponsavel,
            Docente docenteColaborador
    ) {
        return OfertaDisciplina.builder()
                .disciplina(disciplina)
                .docenteResponsavel(docenteResponsavel)
                .docenteColaborador(docenteColaborador)
                .ano(dto.getAno())
                .semestre(dto.getSemestre())
                .periodo(dto.getPeriodo())
                .turma(dto.getTurma() != null ? dto.getTurma() : "A")
                .horarios(dto.getHorarios())
                .sala(dto.getSala())
                .modalidade(dto.getModalidade() != null ? dto.getModalidade() : "Presencial")
                .linkVirtual(dto.getLinkVirtual())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .vagasOferecidas(dto.getVagasOferecidas())
                .vagasOcupadas(0)
                .listaEspera(0)
                .observacoes(dto.getObservacoes())
                .build();
    }

    /**
     * Atualiza uma entidade existente com dados do UpdateDTO
     *
     * @param oferta Entidade a ser atualizada
     * @param dto DTO com dados de atualização
     * @param docenteResponsavel Novo docente responsável (opcional)
     * @param docenteColaborador Novo docente colaborador (opcional)
     */
    public void updateEntity(
            OfertaDisciplina oferta,
            OfertaDisciplinaUpdateDTO dto,
            Docente docenteResponsavel,
            Docente docenteColaborador
    ) {
        if (dto.getDocenteResponsavelId() != null && docenteResponsavel != null) {
            oferta.setDocenteResponsavel(docenteResponsavel);
        }
        if (dto.getDocenteColaboradorId() != null) {
            oferta.setDocenteColaborador(docenteColaborador);
        }
        if (dto.getHorarios() != null) {
            oferta.setHorarios(dto.getHorarios());
        }
        if (dto.getSala() != null) {
            oferta.setSala(dto.getSala());
        }
        if (dto.getModalidade() != null) {
            oferta.setModalidade(dto.getModalidade());
        }
        if (dto.getLinkVirtual() != null) {
            oferta.setLinkVirtual(dto.getLinkVirtual());
        }
        if (dto.getDataInicio() != null) {
            oferta.setDataInicio(dto.getDataInicio());
        }
        if (dto.getDataFim() != null) {
            oferta.setDataFim(dto.getDataFim());
        }
        if (dto.getVagasOferecidas() != null) {
            oferta.setVagasOferecidas(dto.getVagasOferecidas());
        }
        if (dto.getStatus() != null) {
            oferta.setStatus(dto.getStatus());
        }
        if (dto.getObservacoes() != null) {
            oferta.setObservacoes(dto.getObservacoes());
        }
    }

    /**
     * Converte entidade para ResponseDTO
     *
     * @param oferta Entidade OfertaDisciplina
     * @return DTO de resposta
     */
    public OfertaDisciplinaResponseDTO toResponseDTO(OfertaDisciplina oferta) {
        return OfertaDisciplinaResponseDTO.builder()
                .id(oferta.getId())
                .disciplinaId(oferta.getDisciplina().getId())
                .disciplinaCodigo(oferta.getDisciplina().getCodigo())
                .disciplinaNome(oferta.getDisciplina().getNome())
                .disciplinaCreditos(oferta.getDisciplina().getCreditos())
                .docenteResponsavelId(oferta.getDocenteResponsavel().getId())
                .docenteResponsavelNome(oferta.getDocenteResponsavel().getUsuario().getNomeCompleto())
                .docenteResponsavelEmail(oferta.getDocenteResponsavel().getUsuario().getEmail())
                .docenteColaboradorId(oferta.getDocenteColaborador() != null ?
                        oferta.getDocenteColaborador().getId() : null)
                .docenteColaboradorNome(oferta.getDocenteColaborador() != null ?
                        oferta.getDocenteColaborador().getUsuario().getNomeCompleto() : null)
                .docenteColaboradorEmail(oferta.getDocenteColaborador() != null ?
                        oferta.getDocenteColaborador().getUsuario().getEmail() : null)
                .ano(oferta.getAno())
                .semestre(oferta.getSemestre())
                .periodo(oferta.getPeriodo())
                .turma(oferta.getTurma())
                .horarios(oferta.getHorarios())
                .sala(oferta.getSala())
                .modalidade(oferta.getModalidade())
                .linkVirtual(oferta.getLinkVirtual())
                .dataInicio(oferta.getDataInicio())
                .dataFim(oferta.getDataFim())
                .vagasOferecidas(oferta.getVagasOferecidas())
                .vagasOcupadas(oferta.getVagasOcupadas())
                .listaEspera(oferta.getListaEspera())
                .status(oferta.getStatus())
                .observacoes(oferta.getObservacoes())
                .createdAt(oferta.getCreatedAt())
                .updatedAt(oferta.getUpdatedAt())
                // Campos calculados
                .vagasDisponiveis(oferta.calcularVagasDisponiveis())
                .percentualOcupacao(oferta.calcularPercentualOcupacao())
                .temVagasDisponiveis(oferta.temVagasDisponiveis())
                .inscricoesAbertas(oferta.isInscricoesAbertas())
                .ativa(oferta.isAtiva())
                .concluida(oferta.isConcluida())
                .podeCancelar(oferta.podeCancelar())
                .permiteLancarNotas(oferta.permiteLancarNotas())
                .build();
    }
}
