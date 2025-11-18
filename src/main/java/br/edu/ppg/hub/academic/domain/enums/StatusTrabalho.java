package br.edu.ppg.hub.academic.domain.enums;

/**
 * Enum que define os status possíveis de um trabalho de conclusão.
 *
 * @author PPG Hub
 * @version 1.0
 * @since 2025-01-18
 */
public enum StatusTrabalho {

    /**
     * Trabalho em elaboração/preparação
     */
    EM_PREPARACAO("Em Preparação", "Trabalho está sendo elaborado pelo discente"),

    /**
     * Trabalho qualificado (passou pela banca de qualificação)
     */
    QUALIFICADO("Qualificado", "Trabalho foi aprovado na banca de qualificação"),

    /**
     * Trabalho defendido (passou pela banca de defesa)
     */
    DEFENDIDO("Defendido", "Trabalho foi defendido em banca"),

    /**
     * Trabalho aprovado (defesa aprovada, aguardando correções se houver)
     */
    APROVADO("Aprovado", "Trabalho foi aprovado pela banca de defesa"),

    /**
     * Trabalho publicado (versão final depositada no repositório)
     */
    PUBLICADO("Publicado", "Trabalho foi publicado no repositório institucional");

    private final String descricao;
    private final String detalhamento;

    /**
     * Construtor do enum StatusTrabalho.
     *
     * @param descricao Descrição do status
     * @param detalhamento Detalhamento do status
     */
    StatusTrabalho(String descricao, String detalhamento) {
        this.descricao = descricao;
        this.detalhamento = detalhamento;
    }

    /**
     * Obtém a descrição do status.
     *
     * @return Descrição do status
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o detalhamento do status.
     *
     * @return Detalhamento do status
     */
    public String getDetalhamento() {
        return detalhamento;
    }

    /**
     * Verifica se o trabalho pode ser defendido.
     * Apenas trabalhos qualificados podem ser defendidos.
     *
     * @return true se pode ser defendido, false caso contrário
     */
    public boolean podeDefender() {
        return this == QUALIFICADO;
    }

    /**
     * Verifica se o trabalho pode ser publicado.
     * Apenas trabalhos aprovados podem ser publicados.
     *
     * @return true se pode ser publicado, false caso contrário
     */
    public boolean podePublicar() {
        return this == APROVADO;
    }

    /**
     * Verifica se o trabalho está finalizado (defendido, aprovado ou publicado).
     *
     * @return true se está finalizado, false caso contrário
     */
    public boolean isFinalizado() {
        return this == DEFENDIDO || this == APROVADO || this == PUBLICADO;
    }

    /**
     * Verifica se o trabalho está em andamento (em preparação ou qualificado).
     *
     * @return true se está em andamento, false caso contrário
     */
    public boolean isEmAndamento() {
        return this == EM_PREPARACAO || this == QUALIFICADO;
    }
}
