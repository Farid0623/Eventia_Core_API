package cue.edu.co.eventia_core_api.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio
 */
public class BusinessRuleException extends DomainException {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}

