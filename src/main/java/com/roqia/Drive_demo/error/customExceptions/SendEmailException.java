package com.roqia.Drive_demo.error.customExceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;

import jakarta.mail.MessagingException;

public class SendEmailException extends RuntimeException {
    public SendEmailException() {
    }

    public SendEmailException(String message) {
        super(message);
    }
    public SendEmailException(String message, Throwable cause) {
        super(message, cause);
    }

}
