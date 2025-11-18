package br.edu.ppg.hub.academic.application.dto.metrica_docente;

import br.edu.ppg.hub.academic.domain.model.Docente;
import br.edu.ppg.hub.academic.domain.model.MetricaDocente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre MetricaDocente e DTOs
 *
 * @author PPG Hub
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class MetricaDocenteMapper {

    /**
     * Converte CreateDTO para entidade MetricaDocente
     */
    public MetricaDocente toEntity(MetricaDocenteCreateDTO dto, Docente docente) {
        return MetricaDocente.builder()
                .docente(docente)
                .hIndex(dto.getHIndex())
                .totalPublicacoes(dto.getTotalPublicacoes())
                .totalCitacoes(dto.getTotalCitacoes())
                .publicacoesUltimos5Anos(dto.getPublicacoesUltimos5Anos())
                .fonte(dto.getFonte())
                .build();
    }

    /**
     * Converte entidade para ResponseDTO
     */
    public MetricaDocenteResponseDTO toResponseDTO(MetricaDocente metrica) {
        return MetricaDocenteResponseDTO.builder()
                .id(metrica.getId())
                .docenteId(metrica.getDocente().getId())
                .docenteNome(metrica.getDocente().getUsuario().getNomeCompleto())
                .hIndex(metrica.getHIndex())
                .totalPublicacoes(metrica.getTotalPublicacoes())
                .totalCitacoes(metrica.getTotalCitacoes())
                .publicacoesUltimos5Anos(metrica.getPublicacoesUltimos5Anos())
                .fonte(metrica.getFonte())
                .dataColeta(metrica.getDataColeta())
                // Campos calculados
                .mediaCitacoesPorPublicacao(metrica.getMediaCitacoesPorPublicacao())
                .temAltaProdutividade(metrica.temAltaProdutividade())
                .atendeMinimoCapes(metrica.atendeMinimoCapes())
                .build();
    }
}
