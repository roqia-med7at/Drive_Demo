package com.roqia.Drive_demo.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roqia.Drive_demo.error.customExceptions.*;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<?>handleRecordNotFoundException(RecordNotFoundException ex){
       ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), Arrays.asList(ex.getMessage()));
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<?>handleIncorrectPasswordException(IncorrectPasswordException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(),Arrays.asList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(SendEmailException.class)
    public ResponseEntity<String> handleMailExceptions(SendEmailException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(),Arrays.asList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send email: " + errorResponse);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex){
        Map<String,Object> details = new HashMap<>();
        ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(),Arrays.asList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageException(StorageException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(),Arrays.asList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(GoogleProviderException.class)
    public ResponseEntity<?> handleGoogleProviderException(GoogleProviderException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(),Arrays.asList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
