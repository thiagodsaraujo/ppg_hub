package com.ppghub.domain.exception;

/**
 * Exceção lançada quando o rate limit da API OpenAlex é excedido.
 */
public class RateLimitException extends OpenAlexException {

    public RateLimitException(String message) {
        super(message);
    }

    public RateLimitException(Integer retryAfterSeconds) {
        super(String.format("Rate limit excedido. Tente novamente em %d segundos", retryAfterSeconds));
    }
}
