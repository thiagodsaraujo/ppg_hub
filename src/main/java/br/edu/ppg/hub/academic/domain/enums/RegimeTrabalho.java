package br.edu.ppg.hub.academic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Regimes de trabalho dos docentes
 *
 * @author PPG Hub
 * @since 1.0
 */
@Getter
public enum RegimeTrabalho {

    DEDICACAO_EXCLUSIVA("DE", "Dedicação Exclusiva", 40),
    QUARENTA_HORAS("40h", "40 horas semanais", 40),
    VINTE_HORAS("20h", "20 horas semanais", 20);

    private final String codigo;
    private final String descricao;
    private final int horasSemanais;

    RegimeTrabalho(String codigo, String descricao, int horasSemanais) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.horasSemanais = horasSemanais;
    }

    @JsonValue
    public String getCodigo() {
        return codigo;
    }

    /**
     * Converte uma string para o enum correspondente
     *
     * @param valor String a ser convertida
     * @return Enum correspondente
     * @throws IllegalArgumentException se o valor não for válido
     */
    @JsonCreator
    public static RegimeTrabalho fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        for (RegimeTrabalho regime : RegimeTrabalho.values()) {
            if (regime.codigo.equalsIgnoreCase(valor) ||
                regime.name().equalsIgnoreCase(valor) ||
                regime.descricao.equalsIgnoreCase(valor)) {
                return regime;
            }
        }

        throw new IllegalArgumentException(
            "Regime de trabalho inválido: " + valor + ". " +
            "Valores aceitos: DE, 40h, 20h"
        );
    }

    /**
     * Verifica se permite orientação em programas de pós-graduação
     * (geralmente requer DE ou 40h)
     */
    public boolean permiteOrientacao() {
        return this == DEDICACAO_EXCLUSIVA || this == QUARENTA_HORAS;
    }
}
