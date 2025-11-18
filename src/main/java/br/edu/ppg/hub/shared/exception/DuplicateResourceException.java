package br.edu.ppg.hub.shared.exception;

/**
 * Exceção lançada quando há tentativa de criar recurso duplicado.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String field, Object value) {
        super(String.format("%s já existe com %s: %s", resource, field, value));
    }
}
