package com.ppghub.domain.exception;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 * Resulta em HTTP 422 (Unprocessable Entity) para indicar que a requisição
 * está sintaticamente correta mas semanticamente inválida.
 */
public class BusinessRuleException extends PpgHubException {

    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleException(String message, String errorCode) {
        super(message, errorCode);
    }
}
