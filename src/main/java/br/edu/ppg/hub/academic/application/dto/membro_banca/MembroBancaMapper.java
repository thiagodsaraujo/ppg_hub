package br.edu.ppg.hub.academic.application.dto.membro_banca;

import br.edu.ppg.hub.academic.domain.model.Banca;
import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.MembroBanca;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre MembroBanca e DTOs.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
@Component
@RequiredArgsConstructor
public class MembroBancaMapper {

    /**
     * Converte CreateDTO para entidade MembroBanca.
     *
     * @param dto DTO de criação
     * @param banca Banca à qual o membro pertence
     * @param docente Docente (null se for membro externo)
     * @return Entidade MembroBanca
     */
    public MembroBanca toEntity(
            MembroBancaCreateDTO dto,
            Banca banca,
            Docente docente
    ) {
        return MembroBanca.builder()
                .banca(banca)
                .docente(docente)
                .nomeCompleto(dto.getNomeCompleto())
                .instituicao(dto.getInstituicao())
                .titulacao(dto.getTitulacao())
                .email(dto.getEmail())
                .curriculoResumo(dto.getCurriculoResumo())
                .funcao(dto.getFuncao())
                .tipo(dto.getTipo())
                .ordemApresentacao(dto.getOrdemApresentacao())
                .confirmado(false)
                .build();
    }

    /**
     * Converte entidade MembroBanca para ResponseDTO.
     *
     * @param membro Entidade a ser convertida
     * @return DTO de resposta
     */
    public MembroBancaResponseDTO toResponseDTO(MembroBanca membro) {
        return MembroBancaResponseDTO.builder()
                .id(membro.getId())
                .bancaId(membro.getBanca().getId())
                .docenteId(membro.getDocente() != null ? membro.getDocente().getId() : null)
                .docenteNome(membro.getDocente() != null ? membro.getDocente().getNomeCompleto() : null)
                .nomeCompleto(membro.getNomeCompleto())
                .instituicao(membro.getInstituicao())
                .titulacao(membro.getTitulacao())
                .email(membro.getEmail())
                .curriculoResumo(membro.getCurriculoResumo())
                .funcao(membro.getFuncao())
                .tipo(membro.getTipo())
                .ordemApresentacao(membro.getOrdemApresentacao())
                .confirmado(membro.getConfirmado())
                .presente(membro.getPresente())
                .justificativaAusencia(membro.getJustificativaAusencia())
                .parecerIndividual(membro.getParecerIndividual())
                .notaIndividual(membro.getNotaIndividual())
                .arquivoParecer(membro.getArquivoParecer())
                .createdAt(membro.getCreatedAt())
                .build();
    }
}
