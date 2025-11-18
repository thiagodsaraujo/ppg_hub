package br.edu.ppg.hub.core.domain.enums;

/**
 * Modalidade de ensino do programa.
 */
public enum ModalidadePrograma {
    /**
     * Aulas presenciais.
     */
    PRESENCIAL("Presencial"),

    /**
     * Ensino a distância.
     */
    EAD("EAD"),

    /**
     * Parte presencial, parte EAD.
     */
    SEMIPRESENCIAL("Semipresencial");

    private final String descricao;

    ModalidadePrograma(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte string do banco de dados para enum.
     */
    public static ModalidadePrograma fromString(String modalidade) {
        return switch (modalidade) {
            case "Presencial" -> PRESENCIAL;
            case "EAD" -> EAD;
            case "Semipresencial" -> SEMIPRESENCIAL;
            default -> throw new IllegalArgumentException("Modalidade inválida: " + modalidade);
        };
    }
}
