package com.excitel.exception.custom;

public class DynamoDBConnectionException extends RuntimeException{
    public DynamoDBConnectionException(String message) {
        super(message);
    }
}
