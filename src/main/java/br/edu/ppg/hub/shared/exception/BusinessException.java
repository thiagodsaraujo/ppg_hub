package br.edu.ppg.hub.shared.exception;

/**
 * Exceção lançada quando um recurso não é encontrado.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
