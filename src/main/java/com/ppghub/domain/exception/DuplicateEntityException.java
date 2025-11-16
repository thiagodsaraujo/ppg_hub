package com.ppghub.domain.exception;

/**
 * Exceção lançada quando se tenta criar uma entidade que já existe.
 */
public class DuplicateEntityException extends PpgHubException {

    public DuplicateEntityException(String entityName, String field, String value) {
        super(String.format("Já existe %s com %s '%s'", entityName, field, value), "DUPLICATE_ENTITY");
    }

    public DuplicateEntityException(String message) {
        super(message, "DUPLICATE_ENTITY");
    }
}
