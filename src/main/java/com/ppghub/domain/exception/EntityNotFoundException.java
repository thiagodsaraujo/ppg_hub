package com.ppghub.domain.exception;

/**
 * Exceção lançada quando uma entidade não é encontrada.
 */
public class EntityNotFoundException extends PpgHubException {

    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s com ID %d não encontrado(a)", entityName, id), "ENTITY_NOT_FOUND");
    }

    public EntityNotFoundException(String entityName, String field, String value) {
        super(String.format("%s com %s '%s' não encontrado(a)", entityName, field, value), "ENTITY_NOT_FOUND");
    }

    public EntityNotFoundException(String message) {
        super(message, "ENTITY_NOT_FOUND");
    }
}
