package br.edu.ppg.hub.auth.domain.enums;

/**
 * Status de um usuário no sistema.
 */
public enum StatusUsuario {
    /**
     * Usuário ativo e pode fazer login.
     */
    ATIVO("Ativo"),

    /**
     * Usuário inativo (não pode fazer login).
     */
    INATIVO("Inativo"),

    /**
     * Usuário bloqueado (múltiplas tentativas de login falhadas).
     */
    BLOQUEADO("Bloqueado"),

    /**
     * Usuário pendente de verificação de email.
     */
    PENDENTE_VERIFICACAO("Pendente de Verificação"),

    /**
     * Conta suspensa por motivos administrativos.
     */
    SUSPENSO("Suspenso");

    private final String descricao;

    StatusUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
