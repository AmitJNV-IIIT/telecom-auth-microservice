package com.excitel.exception.custom;

public class CustomDynamoDbException extends RuntimeException{
    public CustomDynamoDbException(String message){
        super(message);
    }
}
