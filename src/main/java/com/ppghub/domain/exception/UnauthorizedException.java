package com.ppghub.domain.exception;

/**
 * Exceção lançada quando há erro de autenticação/autorização.
 */
public class UnauthorizedException extends PpgHubException {

    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }

    public UnauthorizedException() {
        super("Acesso não autorizado", "UNAUTHORIZED");
    }
}
