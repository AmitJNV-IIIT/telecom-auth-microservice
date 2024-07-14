package com.excitel.exception.custom;

public class InvalidRequestBodyException extends RuntimeException {
    private String message; //NOSONAR

    public InvalidRequestBodyException(String message) {
        super(message);
    }
}
