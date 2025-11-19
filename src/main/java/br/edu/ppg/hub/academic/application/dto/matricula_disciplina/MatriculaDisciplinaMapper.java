package br.edu.ppg.hub.academic.application.dto.matricula_disciplina;

import br.edu.ppg.hub.academic.domain.model.Discente;
import br.edu.ppg.hub.academic.domain.model.MatriculaDisciplina;
import br.edu.ppg.hub.academic.domain.model.OfertaDisciplina;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre MatriculaDisciplina e DTOs
 *
 * @author PPG Hub
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class MatriculaDisciplinaMapper {

    /**
     * Converte CreateDTO para entidade MatriculaDisciplina
     *
     * @param dto DTO de criação
     * @param ofertaDisciplina Oferta de disciplina
     * @param discente Discente
     * @return Entidade MatriculaDisciplina
     */
    public MatriculaDisciplina toEntity(
            MatriculaDisciplinaCreateDTO dto,
            OfertaDisciplina ofertaDisciplina,
            Discente discente
    ) {
        return MatriculaDisciplina.builder()
                .ofertaDisciplina(ofertaDisciplina)
                .discente(discente)
                .build();
    }

    /**
     * Converte entidade para ResponseDTO
     *
     * @param matricula Entidade MatriculaDisciplina
     * @return DTO de resposta
     */
    public MatriculaDisciplinaResponseDTO toResponseDTO(MatriculaDisciplina matricula) {
        return MatriculaDisciplinaResponseDTO.builder()
                .id(matricula.getId())
                .discenteId(matricula.getDiscente().getId())
                .discenteNome(matricula.getDiscente().getUsuario().getNomeCompleto())
                .discenteEmail(matricula.getDiscente().getUsuario().getEmail())
                .discenteMatricula(matricula.getDiscente().getMatricula())
                .ofertaDisciplinaId(matricula.getOfertaDisciplina().getId())
                .ofertaPeriodo(matricula.getOfertaDisciplina().getPeriodo())
                .ofertaTurma(matricula.getOfertaDisciplina().getTurma())
                .disciplinaId(matricula.getOfertaDisciplina().getDisciplina().getId())
                .disciplinaCodigo(matricula.getOfertaDisciplina().getDisciplina().getCodigo())
                .disciplinaNome(matricula.getOfertaDisciplina().getDisciplina().getNome())
                .disciplinaCreditos(matricula.getOfertaDisciplina().getDisciplina().getCreditos())
                .docenteResponsavelId(matricula.getOfertaDisciplina().getDocenteResponsavel().getId())
                .docenteResponsavelNome(matricula.getOfertaDisciplina().getDocenteResponsavel()
                        .getUsuario().getNomeCompleto())
                .dataMatricula(matricula.getDataMatricula())
                .situacao(matricula.getSituacao())
                .avaliacoes(matricula.getAvaliacoes())
                .frequenciaPercentual(matricula.getFrequenciaPercentual())
                .notaFinal(matricula.getNotaFinal())
                .conceito(matricula.getConceito())
                .statusFinal(matricula.getStatusFinal())
                .dataResultado(matricula.getDataResultado())
                .observacoes(matricula.getObservacoes())
                .createdAt(matricula.getCreatedAt())
                .updatedAt(matricula.getUpdatedAt())
                // Campos calculados
                .aprovado(matricula.isAprovado())
                .reprovado(matricula.isReprovado())
                .ativa(matricula.isAtiva())
                .trancada(matricula.isTrancada())
                .podeTranscar(matricula.podeTranscar())
                .atingiuFrequenciaMinima(matricula.atingiuFrequenciaMinima())
                .atingiuNotaMinima(matricula.atingiuNotaMinima())
                .build();
    }
}
