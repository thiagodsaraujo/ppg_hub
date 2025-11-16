package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para categoria de vínculo de docente em programa.
 */
@Getter
@RequiredArgsConstructor
public enum CategoriaDocente {

    PERMANENTE("Permanente", "Docente permanente do programa"),
    COLABORADOR("Colaborador", "Docente colaborador"),
    VISITANTE("Visitante", "Docente visitante");

    private final String descricao;
    private final String detalhamento;

    public static CategoriaDocente fromString(String categoria) {
        if (categoria == null) {
            return null;
        }
        for (CategoriaDocente c : CategoriaDocente.values()) {
            if (c.name().equalsIgnoreCase(categoria)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Categoria de docente inválida: " + categoria);
    }
}
