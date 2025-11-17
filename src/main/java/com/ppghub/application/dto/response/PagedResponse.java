package com.ppghub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper padronizado para respostas paginadas.
 * Fornece metadata de paginação consistente para facilitar consumo no frontend.
 *
 * @param <T> Tipo do conteúdo paginado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /**
     * Conteúdo da página atual
     */
    private List<T> content;

    /**
     * Número da página atual (começando em 0)
     */
    private int page;

    /**
     * Tamanho da página (número de itens por página)
     */
    private int size;

    /**
     * Total de elementos em todas as páginas
     */
    private long totalElements;

    /**
     * Total de páginas disponíveis
     */
    private int totalPages;

    /**
     * Indica se é a primeira página
     */
    private boolean first;

    /**
     * Indica se é a última página
     */
    private boolean last;

    /**
     * Indica se há próxima página
     */
    private boolean hasNext;

    /**
     * Indica se há página anterior
     */
    private boolean hasPrevious;

    /**
     * Factory method para criar PagedResponse a partir de Spring Data Page
     *
     * @param page Spring Data Page object
     * @param <T> Tipo do conteúdo
     * @return PagedResponse com metadata populated
     */
    public static <T> PagedResponse<T> of(Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
