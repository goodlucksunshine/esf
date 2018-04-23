package com.laile.esf.integrate.exception;

public class ServiceParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceParseException() {
    }

    public ServiceParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceParseException(String message) {
        super(message);
    }

    public ServiceParseException(Throwable cause) {
        super(cause);
    }
}