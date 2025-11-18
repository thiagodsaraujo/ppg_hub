package br.edu.ppg.hub.core.domain.enums;

/**
 * Status de funcionamento do programa.
 */
public enum StatusPrograma {
    /**
     * Programa ativo e funcionando normalmente.
     */
    ATIVO("Ativo"),

    /**
     * Programa temporariamente suspenso.
     */
    SUSPENSO("Suspenso"),

    /**
     * Programa descredenciado pela CAPES.
     */
    DESCREDENCIADO("Descredenciado");

    private final String descricao;

    StatusPrograma(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte string do banco de dados para enum.
     */
    public static StatusPrograma fromString(String status) {
        return switch (status) {
            case "Ativo" -> ATIVO;
            case "Suspenso" -> SUSPENSO;
            case "Descredenciado" -> DESCREDENCIADO;
            default -> throw new IllegalArgumentException("Status inválido: " + status);
        };
    }
}
