package br.edu.ppg.hub.auth.domain.enums;

/**
 * Status de vinculação de um usuário a um programa.
 */
public enum StatusVinculacao {
    /**
     * Vinculação ativa.
     */
    ATIVO("Ativo"),

    /**
     * Vinculação suspensa temporariamente.
     */
    SUSPENSO("Suspenso"),

    /**
     * Usuário desligado do programa.
     */
    DESLIGADO("Desligado");

    private final String descricao;

    StatusVinculacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte string do banco de dados para enum.
     */
    public static StatusVinculacao fromString(String status) {
        return switch (status) {
            case "Ativo" -> ATIVO;
            case "Suspenso" -> SUSPENSO;
            case "Desligado" -> DESLIGADO;
            default -> throw new IllegalArgumentException("Status de vinculação inválido: " + status);
        };
    }
}
