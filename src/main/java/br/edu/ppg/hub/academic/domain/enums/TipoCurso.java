package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Tipos de curso de pós-graduação stricto sensu
 *
 * @author PPG Hub
 * @since 1.0
 */
@Getter
public enum TipoCurso {

    MESTRADO("Mestrado", "Mestrado Acadêmico", 24),
    DOUTORADO("Doutorado", "Doutorado", 48);

    private final String descricao;
    private final String descricaoCompleta;
    private final int prazoMaximoMeses;

    TipoCurso(String descricao, String descricaoCompleta, int prazoMaximoMeses) {
        this.descricao = descricao;
        this.descricaoCompleta = descricaoCompleta;
        this.prazoMaximoMeses = prazoMaximoMeses;
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
    public static TipoCurso fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        for (TipoCurso tipo : TipoCurso.values()) {
            if (tipo.descricao.equalsIgnoreCase(valor) ||
                tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException(
            "Tipo de curso inválido: " + valor + ". " +
            "Valores aceitos: Mestrado, Doutorado"
        );
    }

    /**
     * Retorna o número de créditos mínimos exigidos
     */
    public int getCreditosMinimos() {
        return this == MESTRADO ? 24 : 20;
    }

    /**
     * Verifica se exige qualificação obrigatória
     */
    public boolean exigeQualificacao() {
        return true; // Ambos exigem qualificação
    }
}
