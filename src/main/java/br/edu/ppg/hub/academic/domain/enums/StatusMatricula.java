package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos status possíveis para uma matrícula em disciplina.
 * Define os estados de uma matrícula desde a inscrição até o resultado final.
 *
 * @author Sistema PPG Hub
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum StatusMatricula {

    /**
     * Discente matriculado e cursando
     */
    MATRICULADO("Matriculado", "Discente matriculado e cursando a disciplina"),

    /**
     * Matrícula trancada pelo discente
     */
    TRANCADO("Trancado", "Matrícula trancada, sem resultado final"),

    /**
     * Discente aprovado na disciplina
     */
    APROVADO("Aprovado", "Discente aprovado com sucesso"),

    /**
     * Discente reprovado na disciplina
     */
    REPROVADO("Reprovado", "Discente reprovado por nota ou frequência"),

    /**
     * Discente desistiu da disciplina
     */
    DESISTENTE("Desistente", "Discente desistiu da disciplina"),

    /**
     * Matrícula cancelada
     */
    CANCELADO("Cancelado", "Matrícula cancelada por motivo administrativo");

    /**
     * Valor armazenado no banco de dados
     */
    @JsonValue
    private final String valor;

    /**
     * Descrição do status da matrícula
     */
    private final String descricao;

    /**
     * Retorna a representação em string do enum
     *
     * @return valor do enum
     */
    @Override
    public String toString() {
        return this.valor;
    }

    /**
     * Converte uma string para o enum correspondente
     *
     * @param valor string a ser convertida
     * @return enum correspondente
     * @throws IllegalArgumentException se o valor não foi válido
     */
    @JsonCreator
    public static StatusMatricula fromString(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Status da matrícula não pode ser nulo");
        }

        for (StatusMatricula status : StatusMatricula.values()) {
            if (status.valor.equalsIgnoreCase(valor) || status.name().equalsIgnoreCase(valor)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Status de matrícula inválido: " + valor);
    }

    /**
     * Verifica se o status representa um resultado final
     *
     * @return true se é um status final
     */
    public boolean isFinal() {
        return this == APROVADO || this == REPROVADO || this == TRANCADO ||
               this == DESISTENTE || this == CANCELADO;
    }

    /**
     * Verifica se o discente está cursando ativamente
     *
     * @return true se está cursando
     */
    public boolean isAtivo() {
        return this == MATRICULADO;
    }

    /**
     * Verifica se pode trancar a matrícula
     *
     * @return true se pode trancar
     */
    public boolean podeTranscar() {
        return this == MATRICULADO;
    }

    /**
     * Verifica se é um status de aprovação
     *
     * @return true se foi aprovado
     */
    public boolean isAprovado() {
        return this == APROVADO;
    }
}
