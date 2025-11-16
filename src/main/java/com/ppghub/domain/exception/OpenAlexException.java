package com.ppghub.domain.exception;

/**
 * Exceção base para erros relacionados à integração com OpenAlex.
 */
public class OpenAlexException extends PpgHubException {

    public OpenAlexException(String message) {
        super(message, "OPENALEX_ERROR");
    }

    public OpenAlexException(String message, Throwable cause) {
        super(message, "OPENALEX_ERROR", cause);
    }
}
