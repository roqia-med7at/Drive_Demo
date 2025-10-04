package com.roqia.Drive_demo.error.customExceptions;

public class IncorrectPasswordException extends RuntimeException{
    public IncorrectPasswordException() {
        super();
    }

    public IncorrectPasswordException(String message) {
        super(message);
    }
}
