package br.edu.ppg.hub.core.application.dto.linha_pesquisa;

import br.edu.ppg.hub.auth.domain.model.Usuario;
import br.edu.ppg.hub.core.domain.model.LinhaPesquisa;
import br.edu.ppg.hub.core.domain.model.Programa;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre LinhaPesquisa e seus DTOs.
 */
@Component
public class LinhaPesquisaMapper {

    /**
     * Converte DTO de criação para entidade LinhaPesquisa.
     */
    public LinhaPesquisa toEntity(LinhaPesquisaCreateDTO dto, Programa programa, Usuario coordenador) {
        return LinhaPesquisa.builder()
                .programa(programa)
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .palavrasChave(dto.getPalavrasChave())
                .coordenador(coordenador)
                .ativa(dto.getAtiva())
                .build();
    }

    /**
     * Atualiza entidade LinhaPesquisa com dados do DTO de atualização.
     */
    public void updateEntity(LinhaPesquisa linhaPesquisa, LinhaPesquisaUpdateDTO dto, Usuario coordenador) {
        if (dto.getNome() != null) {
            linhaPesquisa.setNome(dto.getNome());
        }
        if (dto.getDescricao() != null) {
            linhaPesquisa.setDescricao(dto.getDescricao());
        }
        if (dto.getPalavrasChave() != null) {
            linhaPesquisa.setPalavrasChave(dto.getPalavrasChave());
        }
        if (coordenador != null) {
            linhaPesquisa.setCoordenador(coordenador);
        }
        if (dto.getAtiva() != null) {
            linhaPesquisa.setAtiva(dto.getAtiva());
        }
    }

    /**
     * Converte entidade LinhaPesquisa para DTO de resposta.
     */
    public LinhaPesquisaResponseDTO toResponseDTO(LinhaPesquisa linhaPesquisa) {
        return LinhaPesquisaResponseDTO.builder()
                .id(linhaPesquisa.getId())
                .programaId(linhaPesquisa.getPrograma().getId())
                .programaNome(linhaPesquisa.getPrograma().getNome())
                .programaSigla(linhaPesquisa.getPrograma().getSigla())
                .nome(linhaPesquisa.getNome())
                .descricao(linhaPesquisa.getDescricao())
                .palavrasChave(linhaPesquisa.getPalavrasChave())
                .coordenadorId(linhaPesquisa.getCoordenador() != null ? linhaPesquisa.getCoordenador().getId() : null)
                .coordenadorNome(linhaPesquisa.getCoordenador() != null ? linhaPesquisa.getCoordenador().getNomeCompleto() : null)
                .ativa(linhaPesquisa.getAtiva())
                .createdAt(linhaPesquisa.getCreatedAt())
                .updatedAt(linhaPesquisa.getUpdatedAt())
                .build();
    }
}
