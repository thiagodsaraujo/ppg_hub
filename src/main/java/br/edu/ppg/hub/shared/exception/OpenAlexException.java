package br.edu.ppg.hub.shared.exception;

/**
 * Exceção customizada para erros relacionados à integração com OpenAlex.
 *
 * Lançada quando:
 * - Falha na comunicação com a API OpenAlex
 * - Dados inválidos retornados pela API
 * - Erros ao processar respostas do OpenAlex
 * - Limite de requisições excedido
 * - Timeout nas chamadas à API
 *
 * @author PPG Hub
 * @since 1.0
 */
public class OpenAlexException extends RuntimeException {

    /**
     * Cria uma nova exceção OpenAlex com mensagem.
     *
     * @param message mensagem descritiva do erro
     */
    public OpenAlexException(String message) {
        super(message);
    }

    /**
     * Cria uma nova exceção OpenAlex com mensagem e causa.
     *
     * @param message mensagem descritiva do erro
     * @param cause causa raiz da exceção
     */
    public OpenAlexException(String message, Throwable cause) {
        super(message, cause);
    }
}
