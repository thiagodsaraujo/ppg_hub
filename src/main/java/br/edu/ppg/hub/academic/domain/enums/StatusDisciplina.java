package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos status possíveis para uma disciplina.
 * Define os estados do ciclo de vida de uma disciplina no programa.
 *
 * @author Sistema PPG Hub
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum StatusDisciplina {

    /**
     * Disciplina ativa e disponível para ofertas
     */
    ATIVA("Ativa", "Disciplina ativa e disponível para ofertas"),

    /**
     * Disciplina inativa temporariamente
     */
    INATIVA("Inativa", "Disciplina inativa, não disponível para ofertas"),

    /**
     * Disciplina suspensa por decisão administrativa
     */
    SUSPENSA("Suspensa", "Disciplina suspensa, aguardando decisão administrativa"),

    /**
     * Disciplina cancelada permanentemente
     */
    CANCELADA("Cancelada", "Disciplina cancelada permanentemente");

    /**
     * Valor armazenado no banco de dados
     */
    @JsonValue
    private final String valor;

    /**
     * Descrição do status da disciplina
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
     * @throws IllegalArgumentException se o valor não for válido
     */
    @JsonCreator
    public static StatusDisciplina fromString(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Status da disciplina não pode ser nulo");
        }

        for (StatusDisciplina status : StatusDisciplina.values()) {
            if (status.valor.equalsIgnoreCase(valor) || status.name().equalsIgnoreCase(valor)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Status de disciplina inválido: " + valor);
    }

    /**
     * Verifica se a disciplina está ativa
     *
     * @return true se estiver ativa
     */
    public boolean isAtiva() {
        return this == ATIVA;
    }

    /**
     * Verifica se a disciplina pode receber ofertas
     *
     * @return true se pode receber ofertas
     */
    public boolean podeReceberOfertas() {
        return this == ATIVA;
    }
}
