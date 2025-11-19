package br.edu.ppg.hub.shared.exception;

/**
 * Exceção lançada quando há conflito de dados (unicidade, regras de negócio).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
