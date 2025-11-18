package br.edu.ppg.hub.auth.domain.enums;

/**
 * Tipos de roles (papéis) disponíveis no sistema PPG Hub.
 *
 * Hierarquia de níveis de acesso:
 * - SUPERADMIN: Acesso total ao sistema (nível 5)
 * - ADMIN_INSTITUCIONAL: Administrador de uma instituição (nível 4)
 * - COORDENADOR: Coordenador de programa (nível 3)
 * - SECRETARIA: Secretaria do programa (nível 3)
 * - DOCENTE: Professor do programa (nível 2)
 * - DISCENTE: Aluno (mestrado/doutorado) (nível 1)
 * - TECNICO: Técnico administrativo (nível 2)
 * - VISITANTE: Acesso limitado, apenas visualização (nível 1)
 */
public enum TipoRole {
    /**
     * Super administrador do sistema.
     * Acesso total a todas as funcionalidades.
     */
    SUPERADMIN("Super Administrador", 5),

    /**
     * Administrador institucional.
     * Gerencia uma instituição e seus programas.
     */
    ADMIN_INSTITUCIONAL("Administrador Institucional", 4),

    /**
     * Coordenador de programa de pós-graduação.
     * Gerencia um programa específico.
     */
    COORDENADOR("Coordenador", 3),

    /**
     * Secretaria do programa.
     * Auxilia na gestão administrativa do programa.
     */
    SECRETARIA("Secretaria", 3),

    /**
     * Docente (professor) do programa.
     * Pode orientar, ministrar disciplinas, etc.
     */
    DOCENTE("Docente", 2),

    /**
     * Discente (aluno) do programa.
     * Mestrado ou doutorado.
     */
    DISCENTE("Discente", 1),

    /**
     * Técnico administrativo.
     * Suporte técnico e administrativo.
     */
    TECNICO("Técnico", 2),

    /**
     * Visitante.
     * Acesso limitado, apenas visualização.
     */
    VISITANTE("Visitante", 1);

    private final String descricao;
    private final int nivelAcesso;

    TipoRole(String descricao, int nivelAcesso) {
        this.descricao = descricao;
        this.nivelAcesso = nivelAcesso;
    }

    /**
     * Retorna a descrição amigável do tipo de role.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna o nível de acesso (1 a 5).
     * 1 = menor privilégio, 5 = maior privilégio.
     */
    public int getNivelAcesso() {
        return nivelAcesso;
    }

    /**
     * Verifica se este role tem nível de acesso maior ou igual ao especificado.
     *
     * @param nivelMinimo Nível mínimo requerido
     * @return true se tem acesso suficiente
     */
    public boolean temNivelMinimo(int nivelMinimo) {
        return this.nivelAcesso >= nivelMinimo;
    }

    /**
     * Verifica se este role é administrativo (nível >= 3).
     */
    public boolean isAdministrativo() {
        return this.nivelAcesso >= 3;
    }
}
