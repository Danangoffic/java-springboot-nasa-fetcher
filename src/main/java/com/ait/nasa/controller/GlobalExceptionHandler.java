package com.ait.nasa.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ait.nasa.exception.NasaApiException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NasaApiException.class)
        public ResponseEntity<Map<String, Object>> handleNasaApi(NasaApiException ex) {
            HttpStatus status = ex.getStatusCode() == 429
                    ? HttpStatus.TOO_MANY_REQUESTS
                    : HttpStatus.BAD_GATEWAY;
    
            return ResponseEntity.status(status).body(Map.of(
                    "timestamp", Instant.now().toString(),
                    "status", status.value(),
                    "error", ex.getMessage()
            ));
        }
}
