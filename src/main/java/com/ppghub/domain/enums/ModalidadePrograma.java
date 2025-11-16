package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para modalidades de programas de pós-graduação.
 */
@Getter
@RequiredArgsConstructor
public enum ModalidadePrograma {

    ACADEMICO("Acadêmico", "Programa acadêmico (pesquisa)"),
    PROFISSIONAL("Profissional", "Programa profissional (aplicação)");

    private final String descricao;
    private final String detalhamento;

    public static ModalidadePrograma fromString(String modalidade) {
        if (modalidade == null) {
            return null;
        }
        for (ModalidadePrograma m : ModalidadePrograma.values()) {
            if (m.name().equalsIgnoreCase(modalidade)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Modalidade de programa inválida: " + modalidade);
    }
}
