package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para status de programas de pós-graduação.
 */
@Getter
@RequiredArgsConstructor
public enum StatusPrograma {

    ATIVO("Ativo", "Programa em funcionamento"),
    INATIVO("Inativo", "Programa desativado"),
    EM_IMPLANTACAO("Em Implantação", "Programa em processo de implantação"),
    SUSPENSO("Suspenso", "Programa com atividades suspensas");

    private final String descricao;
    private final String detalhamento;

    public static StatusPrograma fromString(String status) {
        if (status == null) {
            return null;
        }
        for (StatusPrograma s : StatusPrograma.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de programa inválido: " + status);
    }
}
