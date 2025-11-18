package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Status do discente no programa
 *
 * @author PPG Hub
 * @since 1.0
 */
@Getter
public enum StatusDiscente {

    MATRICULADO("Matriculado", "Discente matriculado, aguardando início das aulas"),
    CURSANDO("Cursando", "Discente cursando disciplinas"),
    QUALIFICADO("Qualificado", "Discente qualificado, em fase de redação"),
    DEFENDENDO("Defendendo", "Discente com defesa agendada"),
    TITULADO("Titulado", "Discente que concluiu com sucesso"),
    DESLIGADO("Desligado", "Discente desligado do programa");

    private final String descricao;
    private final String detalhamento;

    StatusDiscente(String descricao, String detalhamento) {
        this.descricao = descricao;
        this.detalhamento = detalhamento;
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
    public static StatusDiscente fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        for (StatusDiscente status : StatusDiscente.values()) {
            if (status.descricao.equalsIgnoreCase(valor) ||
                status.name().equalsIgnoreCase(valor)) {
                return status;
            }
        }

        throw new IllegalArgumentException(
            "Status de discente inválido: " + valor + ". " +
            "Valores aceitos: Matriculado, Cursando, Qualificado, Defendendo, Titulado, Desligado"
        );
    }

    /**
     * Verifica se o discente está ativo no programa
     */
    public boolean isAtivo() {
        return this == MATRICULADO || this == CURSANDO || this == QUALIFICADO || this == DEFENDENDO;
    }

    /**
     * Verifica se o discente pode se matricular em disciplinas
     */
    public boolean podeMatricularDisciplinas() {
        return this == MATRICULADO || this == CURSANDO;
    }

    /**
     * Verifica se o discente pode defender
     */
    public boolean podeDefender() {
        return this == QUALIFICADO || this == DEFENDENDO;
    }

    /**
     * Verifica se o status é final (não permite mais alterações)
     */
    public boolean isFinal() {
        return this == TITULADO || this == DESLIGADO;
    }
}
