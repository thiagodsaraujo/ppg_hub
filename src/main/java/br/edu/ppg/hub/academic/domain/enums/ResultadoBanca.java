package br.edu.ppg.hub.academic.domain.enums;

/**
 * Enum que define os resultados possíveis de uma banca examinadora.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
public enum ResultadoBanca {

    /**
     * Aprovado sem restrições
     */
    APROVADO("Aprovado", "Trabalho aprovado sem restrições", true),

    /**
     * Reprovado
     */
    REPROVADO("Reprovado", "Trabalho reprovado pela banca", false),

    /**
     * Aprovado com restrições/correções obrigatórias
     */
    APROVADO_COM_RESTRICOES("Aprovado com Restrições", "Trabalho aprovado com correções obrigatórias", true),

    /**
     * Aprovado com distinção/louvor
     */
    APROVADO_COM_DISTINCAO("Aprovado com Distinção", "Trabalho aprovado com distinção e louvor", true);

    private final String descricao;
    private final String detalhamento;
    private final boolean aprovado;

    /**
     * Construtor do enum ResultadoBanca.
     *
     * @param descricao Descrição do resultado
     * @param detalhamento Detalhamento do resultado
     * @param aprovado Indica se o resultado representa aprovação
     */
    ResultadoBanca(String descricao, String detalhamento, boolean aprovado) {
        this.descricao = descricao;
        this.detalhamento = detalhamento;
        this.aprovado = aprovado;
    }

    /**
     * Obtém a descrição do resultado.
     *
     * @return Descrição do resultado
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o detalhamento do resultado.
     *
     * @return Detalhamento do resultado
     */
    public String getDetalhamento() {
        return detalhamento;
    }

    /**
     * Verifica se o resultado representa aprovação.
     *
     * @return true se aprovado, false se reprovado
     */
    public boolean isAprovado() {
        return aprovado;
    }

    /**
     * Verifica se o resultado é reprovação.
     *
     * @return true se reprovado, false caso contrário
     */
    public boolean isReprovado() {
        return this == REPROVADO;
    }

    /**
     * Verifica se o resultado exige correções.
     *
     * @return true se exige correções, false caso contrário
     */
    public boolean exigeCorrecoes() {
        return this == APROVADO_COM_RESTRICOES;
    }

    /**
     * Verifica se o resultado é aprovação com distinção.
     *
     * @return true se aprovado com distinção, false caso contrário
     */
    public boolean isAprovadoComDistincao() {
        return this == APROVADO_COM_DISTINCAO;
    }

    /**
     * Verifica se o resultado permite a continuidade do discente.
     *
     * @return true se permite continuidade, false caso contrário
     */
    public boolean permiteContinuidade() {
        return aprovado;
    }
}
