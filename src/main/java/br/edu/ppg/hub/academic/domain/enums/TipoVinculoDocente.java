package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Tipos de vínculo do docente com o programa
 *
 * @author PPG Hub
 * @since 1.0
 */
@Getter
public enum TipoVinculoDocente {

    PERMANENTE("Permanente", "Docente permanente do programa"),
    COLABORADOR("Colaborador", "Docente colaborador"),
    VISITANTE("Visitante", "Docente visitante"),
    VOLUNTARIO("Voluntário", "Docente voluntário");

    private final String descricao;
    private final String detalhamento;

    TipoVinculoDocente(String descricao, String detalhamento) {
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
    public static TipoVinculoDocente fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        for (TipoVinculoDocente tipo : TipoVinculoDocente.values()) {
            if (tipo.descricao.equalsIgnoreCase(valor) ||
                tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException(
            "Tipo de vínculo inválido: " + valor + ". " +
            "Valores aceitos: Permanente, Colaborador, Visitante, Voluntário"
        );
    }

    /**
     * Verifica se é docente permanente (para fins de avaliação CAPES)
     */
    public boolean isPermanente() {
        return this == PERMANENTE;
    }

    /**
     * Verifica se pode orientar sozinho (sem coorientação)
     */
    public boolean podeOrientarSozinho() {
        return this == PERMANENTE || this == COLABORADOR;
    }
}
