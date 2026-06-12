package com.ait.nasa.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.ait.nasa.exception.ApiError;
import com.ait.nasa.exception.InvalidDateRangeException;
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
                                "error", ex.getMessage()));
        }

        // Rentang tanggal invalid (start > end, atau > 7 hari)
        @ExceptionHandler(InvalidDateRangeException.class)
        public ResponseEntity<ApiError> handleInvalidRange(InvalidDateRangeException ex) {
                return badRequest(ex.getMessage());
        }

        // Format tanggal salah, mis. "2026-13-40" atau "bukan-tanggal"
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
                return badRequest("Parameter '" + ex.getName()
                        + "' tidak valid. Gunakan format tanggal YYYY-MM-DD");
        }

        // Query param wajib tidak dikirim
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex) {
                return badRequest("Parameter '" + ex.getParameterName() + "' wajib diisi");
        }

        private ResponseEntity<ApiError> badRequest(String message) {
                return ResponseEntity
                        .badRequest()
                        .body(ApiError.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", message));
        }
}
