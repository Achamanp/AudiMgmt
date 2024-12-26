package com.InvertisAuditoriumManagement.AudiMgmt.globalexception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private String message;
    private long timestamp;
    public ErrorResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    public ErrorResponse(HttpStatus status, String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
