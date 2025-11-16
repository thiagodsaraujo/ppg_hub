package com.ppghub.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum para titulação acadêmica.
 */
@Getter
@RequiredArgsConstructor
public enum Titulacao {

    GRADUACAO("Graduação", "Graduado"),
    ESPECIALIZACAO("Especialização", "Especialista"),
    MESTRADO("Mestrado", "Mestre"),
    DOUTORADO("Doutorado", "Doutor"),
    POS_DOUTORADO("Pós-Doutorado", "Pós-Doutor"),
    LIVRE_DOCENCIA("Livre Docência", "Livre Docente");

    private final String descricao;
    private final String detalhamento;

    public static Titulacao fromString(String titulacao) {
        if (titulacao == null) {
            return null;
        }
        for (Titulacao t : Titulacao.values()) {
            if (t.name().equalsIgnoreCase(titulacao)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Titulação inválida: " + titulacao);
    }
}
