package com.ait.nasa.exception;

public class NasaApiException extends RuntimeException {
    private final int statusCode;

    public NasaApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
