package com.excitel.exception.global;

import com.excitel.dto.ErrorObject;
import com.excitel.exception.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;

/**
 Global Exception Handler class to handle all the exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     Handles RegistrationFailedException.
     @param ex The exception
     @param request The web request
     @return ResponseEntity with the status and error message
     */
    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ErrorObject> handleRegistrationException(RegistrationFailedException ex, WebRequest request){
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     Handles CustomerAlreadyExistsException.
     @param exception The exception
     @param request The web request
     @return ResponseEntity with the status and error message
     */
    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ErrorObject> handleCustomerAlreadyExists(CustomerAlreadyExistsException exception, WebRequest request){
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(HttpStatus.BAD_REQUEST.toString());
        errorObject.setMessage(exception.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    /**
     Handles UserNotFoundException.
     @param ex The exception
     @param request The web request
     @return ResponseEntity with the status and error message
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorObject> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(HttpStatus.NOT_FOUND.toString());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimestamp(new Date());
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    /**
     Handles UserServiceException.
     @param exception The exception
     @param request The web request
     @return ResponseEntity with the status and error message
     */
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorObject> handleUserServiceException(UserServiceException exception, WebRequest request){
        ErrorObject errorObject = new ErrorObject();


        errorObject.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorObject.setMessage(exception.getMessage().split(":")[1]);
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     Handles CustomAuthenticationException.
     @param ex The exception
     @return ResponseEntity with the status and error message
     */
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ErrorObject> handleCustomAuthenticationException(CustomAuthenticationException ex) {
        ErrorObject error = new ErrorObject();
        error.setStatus(HttpStatus.UNAUTHORIZED.toString());
        error.setMessage(ex.getMessage());
        error.setTimestamp(new Date());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     Handles InvalidRequestBodyException.
     @param ex The exception
     @return ResponseEntity with the status and error message
     */
    @ExceptionHandler(InvalidRequestBodyException.class)
    public ResponseEntity<ErrorObject> invalidRequestBodyException(InvalidRequestBodyException ex) {
        ErrorObject error = new ErrorObject();
        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        error.setMessage(ex.getMessage());
        error.setTimestamp(new Date());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     Handles CustomDynamoDbException.
     @param ex The exception
     @return ResponseEntity with the status and error message
     */
    @ExceptionHandler(CustomDynamoDbException.class)
    public ResponseEntity<ErrorObject> handleCustomDynamoDbException(CustomDynamoDbException ex) {
        ErrorObject error = new ErrorObject();
        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        error.setMessage(ex.getMessage());
        error.setTimestamp(new Date());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}