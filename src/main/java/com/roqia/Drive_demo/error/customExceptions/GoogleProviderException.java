package com.roqia.Drive_demo.error.customExceptions;

public class GoogleProviderException extends RuntimeException{
    public GoogleProviderException() {
    }

    public GoogleProviderException(String message) {
        super(message);
    }

    public GoogleProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
