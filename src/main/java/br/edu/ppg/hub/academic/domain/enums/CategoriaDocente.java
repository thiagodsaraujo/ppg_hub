package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Categorias de docentes conforme hierarquia acadêmica
 *
 * @author PPG Hub
 * @since 1.0
 */
@Getter
public enum CategoriaDocente {

    TITULAR("Professor Titular", "Titular"),
    ASSOCIADO("Professor Associado", "Associado"),
    ADJUNTO("Professor Adjunto", "Adjunto"),
    ASSISTENTE("Professor Assistente", "Assistente");

    private final String descricao;
    private final String descricaoCurta;

    CategoriaDocente(String descricao, String descricaoCurta) {
        this.descricao = descricao;
        this.descricaoCurta = descricaoCurta;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte uma string para o enum correspondente
     *
     * @param valor String a ser convertida
     * @return Enum correspondente
     * @throws IllegalArgumentException se o valor não for válido
     */
    @JsonCreator
    public static CategoriaDocente fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        for (CategoriaDocente categoria : CategoriaDocente.values()) {
            if (categoria.descricao.equalsIgnoreCase(valor) ||
                categoria.name().equalsIgnoreCase(valor) ||
                categoria.descricaoCurta.equalsIgnoreCase(valor)) {
                return categoria;
            }
        }

        throw new IllegalArgumentException(
            "Categoria de docente inválida: " + valor + ". " +
            "Valores aceitos: Professor Titular, Professor Associado, Professor Adjunto, Professor Assistente"
        );
    }

    /**
     * Verifica se é uma categoria superior (Titular ou Associado)
     */
    public boolean isCategoriaSuperior() {
        return this == TITULAR || this == ASSOCIADO;
    }
}
