package com.roqia.Drive_demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class SuccessResponse<T> {
    private  String message;
    private LocalDateTime timestamp;
    private T details;
    private Boolean success;

    public T getDetails() {
        return details;
    }

    public void setDetails(T details) {
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public SuccessResponse(String message, T details) {
        super();
        this.message=message;
        this.details=details;
        this.success=Boolean.TRUE;
        this.timestamp=LocalDateTime.now();
    }
}
