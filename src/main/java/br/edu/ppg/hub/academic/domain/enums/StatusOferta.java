package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos status possíveis para uma oferta de disciplina.
 * Define os estados do ciclo de vida de uma oferta desde o planejamento até a conclusão.
 *
 * @author Sistema PPG Hub
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum StatusOferta {

    /**
     * Oferta está em fase de planejamento
     */
    PLANEJADA("Planejada", "Oferta planejada, ainda não aberta para inscrições"),

    /**
     * Oferta com inscrições abertas
     */
    ABERTA("Aberta", "Inscrições abertas para matrícula"),

    /**
     * Oferta com inscrições fechadas
     */
    FECHADA("Fechada", "Inscrições fechadas, aguardando início"),

    /**
     * Oferta em andamento
     */
    EM_CURSO("Em_Curso", "Disciplina em andamento"),

    /**
     * Oferta concluída
     */
    CONCLUIDA("Concluída", "Disciplina concluída com notas lançadas"),

    /**
     * Oferta cancelada
     */
    CANCELADA("Cancelada", "Oferta cancelada antes da conclusão");

    /**
     * Valor armazenado no banco de dados
     */
    @JsonValue
    private final String valor;

    /**
     * Descrição do status da oferta
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
    public static StatusOferta fromString(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Status da oferta não pode ser nulo");
        }

        for (StatusOferta status : StatusOferta.values()) {
            if (status.valor.equalsIgnoreCase(valor) || status.name().equalsIgnoreCase(valor)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Status de oferta inválido: " + valor);
    }

    /**
     * Verifica se a oferta está aberta para inscrições
     *
     * @return true se as inscrições estão abertas
     */
    public boolean isInscricoesAbertas() {
        return this == ABERTA;
    }

    /**
     * Verifica se a oferta pode ser cancelada
     *
     * @return true se pode ser cancelada
     */
    public boolean podeCancelar() {
        return this != CONCLUIDA && this != CANCELADA;
    }

    /**
     * Verifica se a oferta permite lançamento de notas
     *
     * @return true se permite lançamento de notas
     */
    public boolean permiteLancarNotas() {
        return this == EM_CURSO || this == CONCLUIDA;
    }

    /**
     * Verifica se a oferta está ativa
     *
     * @return true se está em curso ou aberta
     */
    public boolean isAtiva() {
        return this == ABERTA || this == EM_CURSO || this == FECHADA;
    }
}
