package com.excitel.exception.custom;

public class RegistrationFailedException extends RuntimeException {
    public RegistrationFailedException(String message) {
        super(message);
    }
    public RegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
