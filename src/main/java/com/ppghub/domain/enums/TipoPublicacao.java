package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para tipos de publicação científica.
 */
@Getter
@RequiredArgsConstructor
public enum TipoPublicacao {

    ARTICLE("article", "Artigo de periódico"),
    BOOK("book", "Livro"),
    BOOK_CHAPTER("book-chapter", "Capítulo de livro"),
    PROCEEDINGS_ARTICLE("proceedings-article", "Artigo em anais de evento"),
    DISSERTATION("dissertation", "Dissertação"),
    PARATEXT("paratext", "Paratexto"),
    OTHER("other", "Outro tipo");

    private final String codigo;
    private final String descricao;

    public static TipoPublicacao fromCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        for (TipoPublicacao t : TipoPublicacao.values()) {
            if (t.getCodigo().equalsIgnoreCase(codigo)) {
                return t;
            }
        }
        return OTHER;
    }

    public static TipoPublicacao fromString(String tipo) {
        if (tipo == null) {
            return null;
        }
        for (TipoPublicacao t : TipoPublicacao.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de publicação inválido: " + tipo);
    }
}
