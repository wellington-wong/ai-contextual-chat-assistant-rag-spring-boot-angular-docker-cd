package com.ragassistant.controller;
import com.ragassistant.dto.Dtos;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.IOException;

@RestControllerAdvice

public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Dtos.ApiError> handleRuntime(RuntimeException e) {
        log.error("Runtime error", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Dtos.ApiError(e.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Dtos.ApiError> handleIo(IOException e) {
        log.error("IO / API error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new Dtos.ApiError("External API error: " + e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Dtos.ApiError> handleGeneral(Exception e) {

        log.error("Unexpected error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Dtos.ApiError("Internal server error"));
    }
}