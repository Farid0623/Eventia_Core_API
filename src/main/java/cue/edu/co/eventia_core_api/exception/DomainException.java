package cue.edu.co.eventia_core_api.exception;

/**
 * Excepción base para el dominio
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

