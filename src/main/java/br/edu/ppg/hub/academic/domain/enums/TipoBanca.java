package br.edu.ppg.hub.academic.domain.enums;

/**
 * Enum que define os tipos de bancas examinadoras.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
public enum TipoBanca {

    /**
     * Banca de Qualificação (avaliação do projeto de pesquisa)
     */
    QUALIFICACAO("Qualificação", "Banca de qualificação do projeto de pesquisa", 3),

    /**
     * Banca de Defesa de Dissertação de Mestrado
     */
    DEFESA_DISSERTACAO("Defesa de Dissertação", "Banca de defesa de dissertação de mestrado", 5),

    /**
     * Banca de Defesa de Tese de Doutorado
     */
    DEFESA_TESE("Defesa de Tese", "Banca de defesa de tese de doutorado", 7);

    private final String descricao;
    private final String detalhamento;
    private final int numeroMinimoMembros;

    /**
     * Construtor do enum TipoBanca.
     *
     * @param descricao Descrição do tipo de banca
     * @param detalhamento Detalhamento do tipo de banca
     * @param numeroMinimoMembros Número mínimo de membros requeridos
     */
    TipoBanca(String descricao, String detalhamento, int numeroMinimoMembros) {
        this.descricao = descricao;
        this.detalhamento = detalhamento;
        this.numeroMinimoMembros = numeroMinimoMembros;
    }

    /**
     * Obtém a descrição do tipo de banca.
     *
     * @return Descrição do tipo de banca
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o detalhamento do tipo de banca.
     *
     * @return Detalhamento do tipo de banca
     */
    public String getDetalhamento() {
        return detalhamento;
    }

    /**
     * Obtém o número mínimo de membros requeridos.
     *
     * @return Número mínimo de membros
     */
    public int getNumeroMinimoMembros() {
        return numeroMinimoMembros;
    }

    /**
     * Verifica se é uma banca de qualificação.
     *
     * @return true se for qualificação, false caso contrário
     */
    public boolean isQualificacao() {
        return this == QUALIFICACAO;
    }

    /**
     * Verifica se é uma banca de defesa (dissertação ou tese).
     *
     * @return true se for defesa, false caso contrário
     */
    public boolean isDefesa() {
        return this == DEFESA_DISSERTACAO || this == DEFESA_TESE;
    }

    /**
     * Verifica se é uma banca de defesa de tese.
     *
     * @return true se for defesa de tese, false caso contrário
     */
    public boolean isDefesaTese() {
        return this == DEFESA_TESE;
    }

    /**
     * Verifica se é uma banca de defesa de dissertação.
     *
     * @return true se for defesa de dissertação, false caso contrário
     */
    public boolean isDefesaDissertacao() {
        return this == DEFESA_DISSERTACAO;
    }
}
