package br.edu.ppg.hub.core.domain.enums;

/**
 * Nível de pós-graduação do programa.
 */
public enum NivelPrograma {
    /**
     * Programa oferece apenas mestrado.
     */
    MESTRADO("Mestrado"),

    /**
     * Programa oferece apenas doutorado.
     */
    DOUTORADO("Doutorado"),

    /**
     * Programa oferece mestrado e doutorado.
     */
    MESTRADO_DOUTORADO("Mestrado/Doutorado");

    private final String descricao;

    NivelPrograma(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte string do banco de dados para enum.
     */
    public static NivelPrograma fromString(String nivel) {
        return switch (nivel) {
            case "Mestrado" -> MESTRADO;
            case "Doutorado" -> DOUTORADO;
            case "Mestrado/Doutorado" -> MESTRADO_DOUTORADO;
            default -> throw new IllegalArgumentException("Nível inválido: " + nivel);
        };
    }
}
