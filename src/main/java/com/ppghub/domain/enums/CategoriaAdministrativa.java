package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para categoria administrativa de instituições.
 */
@Getter
@RequiredArgsConstructor
public enum CategoriaAdministrativa {

    FEDERAL("Federal", "Instituição federal"),
    ESTADUAL("Estadual", "Instituição estadual"),
    MUNICIPAL("Municipal", "Instituição municipal"),
    PARTICULAR("Particular", "Instituição particular"),
    COMUNITARIA("Comunitária", "Instituição comunitária"),
    CONFESSIONAL("Confessional", "Instituição confessional"),
    FILANTROPICA("Filantrópica", "Instituição filantrópica");

    private final String descricao;
    private final String detalhamento;

    public static CategoriaAdministrativa fromString(String categoria) {
        if (categoria == null) {
            return null;
        }
        for (CategoriaAdministrativa c : CategoriaAdministrativa.values()) {
            if (c.name().equalsIgnoreCase(categoria)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Categoria administrativa inválida: " + categoria);
    }
}
