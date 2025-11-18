package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Status do docente no programa
 *
 * @author PPG Hub
 * @since 1.0
 */
@Getter
public enum StatusDocente {

    ATIVO("Ativo", "Docente ativo no programa"),
    AFASTADO("Afastado", "Docente temporariamente afastado"),
    APOSENTADO("Aposentado", "Docente aposentado"),
    DESLIGADO("Desligado", "Docente desligado do programa");

    private final String descricao;
    private final String detalhamento;

    StatusDocente(String descricao, String detalhamento) {
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
    public static StatusDocente fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        for (StatusDocente status : StatusDocente.values()) {
            if (status.descricao.equalsIgnoreCase(valor) ||
                status.name().equalsIgnoreCase(valor)) {
                return status;
            }
        }

        throw new IllegalArgumentException(
            "Status de docente inválido: " + valor + ". " +
            "Valores aceitos: Ativo, Afastado, Aposentado, Desligado"
        );
    }

    /**
     * Verifica se o docente pode orientar novos alunos
     */
    public boolean podeOrientar() {
        return this == ATIVO;
    }

    /**
     * Verifica se o docente está vinculado ao programa
     */
    public boolean isVinculado() {
        return this == ATIVO || this == AFASTADO;
    }
}
