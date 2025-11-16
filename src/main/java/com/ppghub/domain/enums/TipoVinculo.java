package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para tipo de vínculo do docente.
 */
@Getter
@RequiredArgsConstructor
public enum TipoVinculo {

    EFETIVO("Efetivo", "Vínculo efetivo"),
    TEMPORARIO("Temporário", "Vínculo temporário"),
    VOLUNTARIO("Voluntário", "Vínculo voluntário"),
    BOLSISTA("Bolsista", "Bolsista"),
    EXTERNO("Externo", "Vínculo externo");

    private final String descricao;
    private final String detalhamento;

    public static TipoVinculo fromString(String tipo) {
        if (tipo == null) {
            return null;
        }
        for (TipoVinculo t : TipoVinculo.values()) {
            if (t.name().equalsIgnoreCase(tipo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de vínculo inválido: " + tipo);
    }
}
