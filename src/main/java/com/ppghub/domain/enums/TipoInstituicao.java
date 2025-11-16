package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para tipos de instituições de ensino superior.
 */
@Getter
@RequiredArgsConstructor
public enum TipoInstituicao {

    PUBLICA("Pública", "Instituição de ensino público"),
    PRIVADA("Privada", "Instituição de ensino privado"),
    ESPECIAL("Especial", "Instituição de regime especial");

    private final String descricao;
    private final String detalhamento;

    /**
     * Converte string para enum (case-insensitive)
     */
    public static TipoInstituicao fromString(String tipo) {
        if (tipo == null) {
            return null;
        }
        for (TipoInstituicao t : TipoInstituicao.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de instituição inválido: " + tipo);
    }
}
