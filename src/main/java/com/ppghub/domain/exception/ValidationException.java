package com.ppghub.domain.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Exceção lançada quando há erros de validação de dados.
 */
@Getter
public class ValidationException extends PpgHubException {

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.errors = new HashMap<>();
    }

    public ValidationException(String field, String message) {
        super(message, "VALIDATION_ERROR");
        this.errors = new HashMap<>();
        this.errors.put(field, message);
    }

    public ValidationException(Map<String, String> errors) {
        super("Erro de validação dos dados fornecidos", "VALIDATION_ERROR");
        this.errors = errors;
    }

    public void addError(String field, String message) {
        this.errors.put(field, message);
    }
}
