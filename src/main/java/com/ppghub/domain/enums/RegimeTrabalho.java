package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para regime de trabalho do docente.
 */
@Getter
@RequiredArgsConstructor
public enum RegimeTrabalho {

    DEDICACAO_EXCLUSIVA("Dedicação Exclusiva", "40 horas semanais em regime de dedicação exclusiva"),
    TEMPO_INTEGRAL("Tempo Integral", "40 horas semanais"),
    TEMPO_PARCIAL("Tempo Parcial", "20 horas semanais ou menos"),
    HORISTA("Horista", "Remunerado por hora-aula");

    private final String descricao;
    private final String detalhamento;

    public static RegimeTrabalho fromString(String regime) {
        if (regime == null) {
            return null;
        }
        for (RegimeTrabalho r : RegimeTrabalho.values()) {
            if (r.name().equalsIgnoreCase(regime)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Regime de trabalho inválido: " + regime);
    }
}
