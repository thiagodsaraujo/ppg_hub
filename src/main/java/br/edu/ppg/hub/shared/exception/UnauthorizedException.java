package br.edu.ppg.hub.shared.exception;

/**
 * Exceção lançada quando um usuário não tem permissão para realizar uma operação.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
