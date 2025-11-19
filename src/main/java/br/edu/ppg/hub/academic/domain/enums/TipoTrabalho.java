package br.edu.ppg.hub.academic.domain.enums;

/**
 * Enum que define os tipos de trabalhos de conclusão.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
public enum TipoTrabalho {

    /**
     * Dissertação de Mestrado
     */
    DISSERTACAO_MESTRADO("Dissertação de Mestrado", "Trabalho acadêmico apresentado como requisito parcial para obtenção do título de Mestre"),

    /**
     * Tese de Doutorado
     */
    TESE_DOUTORADO("Tese de Doutorado", "Trabalho acadêmico original apresentado como requisito parcial para obtenção do título de Doutor"),

    /**
     * Artigo científico
     */
    ARTIGO("Artigo", "Artigo científico publicado ou submetido para publicação"),

    /**
     * Projeto de pesquisa
     */
    PROJETO("Projeto", "Projeto de pesquisa ou desenvolvimento tecnológico");

    private final String descricao;
    private final String detalhamento;

    /**
     * Construtor do enum TipoTrabalho.
     *
     * @param descricao Descrição do tipo de trabalho
     * @param detalhamento Detalhamento do tipo de trabalho
     */
    TipoTrabalho(String descricao, String detalhamento) {
        this.descricao = descricao;
        this.detalhamento = detalhamento;
    }

    /**
     * Obtém a descrição do tipo de trabalho.
     *
     * @return Descrição do tipo de trabalho
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o detalhamento do tipo de trabalho.
     *
     * @return Detalhamento do tipo de trabalho
     */
    public String getDetalhamento() {
        return detalhamento;
    }

    /**
     * Verifica se o tipo de trabalho é dissertação de mestrado.
     *
     * @return true se for dissertação de mestrado, false caso contrário
     */
    public boolean isDissertacao() {
        return this == DISSERTACAO_MESTRADO;
    }

    /**
     * Verifica se o tipo de trabalho é tese de doutorado.
     *
     * @return true se for tese de doutorado, false caso contrário
     */
    public boolean isTese() {
        return this == TESE_DOUTORADO;
    }

    /**
     * Verifica se o tipo de trabalho é acadêmico tradicional (dissertação ou tese).
     *
     * @return true se for dissertação ou tese, false caso contrário
     */
    public boolean isTrabalhoAcademicoTradicional() {
        return this == DISSERTACAO_MESTRADO || this == TESE_DOUTORADO;
    }
}
