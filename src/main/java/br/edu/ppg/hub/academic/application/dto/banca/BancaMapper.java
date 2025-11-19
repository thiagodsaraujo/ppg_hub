package br.edu.ppg.hub.academic.application.dto.banca;

import br.edu.ppg.hub.academic.domain.model.Banca;
import br.edu.ppg.hub.academic.domain.model.Discente;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.TrabalhoConclusao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Banca e DTOs.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Component
@RequiredArgsConstructor
public class BancaMapper {

    /**
     * Converte CreateDTO para entidade Banca.
     *
     * @param dto DTO de criação
     * @param trabalhoConclusao Trabalho de conclusão (pode ser null para qualificação)
     * @param discente Discente que será avaliado
     * @param presidente Presidente da banca
     * @param secretario Secretário da banca (pode ser null)
     * @return Entidade Banca
     */
    public Banca toEntity(
            BancaCreateDTO dto,
            TrabalhoConclusao trabalhoConclusao,
            Discente discente,
            Docente presidente,
            Docente secretario
    ) {
        return Banca.builder()
                .trabalhoConclusao(trabalhoConclusao)
                .discente(discente)
                .presidente(presidente)
                .secretario(secretario)
                .tipo(dto.getTipo())
                .dataAgendada(dto.getDataAgendada())
                .horarioInicio(dto.getHorarioInicio())
                .horarioFim(dto.getHorarioFim())
                .localRealizacao(dto.getLocalRealizacao())
                .modalidade(dto.getModalidade() != null ? dto.getModalidade() : "Presencial")
                .linkVirtual(dto.getLinkVirtual())
                .pauta(dto.getPauta())
                .status("Agendada")
                .build();
    }

    /**
     * Atualiza uma entidade existente com dados do UpdateDTO.
     *
     * @param banca Entidade a ser atualizada
     * @param dto DTO com dados de atualização
     * @param presidente Novo presidente (se fornecido)
     * @param secretario Novo secretário (se fornecido)
     */
    public void updateEntity(
            Banca banca,
            BancaUpdateDTO dto,
            Docente presidente,
            Docente secretario
    ) {
        if (dto.getDataAgendada() != null) {
            banca.setDataAgendada(dto.getDataAgendada());
        }

        if (dto.getHorarioInicio() != null) {
            banca.setHorarioInicio(dto.getHorarioInicio());
        }

        if (dto.getHorarioFim() != null) {
            banca.setHorarioFim(dto.getHorarioFim());
        }

        if (dto.getLocalRealizacao() != null) {
            banca.setLocalRealizacao(dto.getLocalRealizacao());
        }

        if (dto.getModalidade() != null) {
            banca.setModalidade(dto.getModalidade());
        }

        if (dto.getLinkVirtual() != null) {
            banca.setLinkVirtual(dto.getLinkVirtual());
        }

        if (dto.getPresidenteId() != null && presidente != null) {
            banca.setPresidente(presidente);
        }

        if (dto.getSecretarioId() != null) {
            banca.setSecretario(secretario);
        }

        if (dto.getAtaNumero() != null) {
            banca.setAtaNumero(dto.getAtaNumero());
        }

        if (dto.getPauta() != null) {
            banca.setPauta(dto.getPauta());
        }

        if (dto.getAta() != null) {
            banca.setAta(dto.getAta());
        }

        if (dto.getResultado() != null) {
            banca.setResultado(dto.getResultado());
        }

        if (dto.getNotaFinal() != null) {
            banca.setNotaFinal(dto.getNotaFinal());
        }

        if (dto.getPrazoCorrecoesDias() != null) {
            banca.setPrazoCorrecoesDias(dto.getPrazoCorrecoesDias());
        }

        if (dto.getCorrecoesExigidas() != null) {
            banca.setCorrecoesExigidas(dto.getCorrecoesExigidas());
        }

        if (dto.getObservacoesBanca() != null) {
            banca.setObservacoesBanca(dto.getObservacoesBanca());
        }

        if (dto.getRecomendacoes() != null) {
            banca.setRecomendacoes(dto.getRecomendacoes());
        }

        if (dto.getSugestaoPublicacao() != null) {
            banca.setSugestaoPublicacao(dto.getSugestaoPublicacao());
        }
    }

    /**
     * Converte entidade Banca para ResponseDTO.
     *
     * @param banca Entidade a ser convertida
     * @return DTO de resposta
     */
    public BancaResponseDTO toResponseDTO(Banca banca) {
        return BancaResponseDTO.builder()
                .id(banca.getId())
                .trabalhoConclusaoId(banca.getTrabalhoConclusao() != null ? banca.getTrabalhoConclusao().getId() : null)
                .trabalhoConclusaoTitulo(banca.getTrabalhoConclusao() != null ? banca.getTrabalhoConclusao().getTituloPortugues() : null)
                .discenteId(banca.getDiscente().getId())
                .discenteNome(banca.getDiscente().getUsuario().getNomeCompleto())
                .presidenteId(banca.getPresidente().getId())
                .presidenteNome(banca.getPresidente().getNomeCompleto())
                .secretarioId(banca.getSecretario() != null ? banca.getSecretario().getId() : null)
                .secretarioNome(banca.getSecretario() != null ? banca.getSecretario().getNomeCompleto() : null)
                .tipo(banca.getTipo())
                .status(banca.getStatus())
                .dataAgendada(banca.getDataAgendada())
                .horarioInicio(banca.getHorarioInicio())
                .horarioFim(banca.getHorarioFim())
                .localRealizacao(banca.getLocalRealizacao())
                .modalidade(banca.getModalidade())
                .linkVirtual(banca.getLinkVirtual())
                .dataRealizacao(banca.getDataRealizacao())
                .ataNumero(banca.getAtaNumero())
                .ataArquivo(banca.getAtaArquivo())
                .pauta(banca.getPauta())
                .ata(banca.getAta())
                .resultado(banca.getResultado())
                .notaFinal(banca.getNotaFinal())
                .prazoCorrecoesDias(banca.getPrazoCorrecoesDias())
                .correcoesExigidas(banca.getCorrecoesExigidas())
                .observacoesBanca(banca.getObservacoesBanca())
                .recomendacoes(banca.getRecomendacoes())
                .sugestaoPublicacao(banca.getSugestaoPublicacao())
                .createdAt(banca.getCreatedAt())
                .updatedAt(banca.getUpdatedAt())
                .build();
    }
}
