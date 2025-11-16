package cue.edu.co.eventia_core_api.exception;

/**
 * Excepción lanzada cuando ya existe un recurso duplicado
 */
public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s ya existe con %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}

