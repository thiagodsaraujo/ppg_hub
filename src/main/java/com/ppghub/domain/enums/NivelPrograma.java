package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para níveis de programas de pós-graduação.
 */
@Getter
@RequiredArgsConstructor
public enum NivelPrograma {

    MESTRADO("Mestrado", "Apenas mestrado"),
    DOUTORADO("Doutorado", "Apenas doutorado"),
    MESTRADO_DOUTORADO("Mestrado e Doutorado", "Mestrado e doutorado");

    private final String descricao;
    private final String detalhamento;

    public static NivelPrograma fromString(String nivel) {
        if (nivel == null) {
            return null;
        }
        for (NivelPrograma n : NivelPrograma.values()) {
            if (n.name().equalsIgnoreCase(nivel)) {
                return n;
            }
        }
        throw new IllegalArgumentException("Nível de programa inválido: " + nivel);
    }
}
