package com.example.monitoring.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Global exception handler for the application.
 *
 * Responsibilities:
 * - Convert exceptions into structured API responses
 * - Ensure consistent error format across APIs
 * - Log errors with appropriate severity levels
 * - Support request tracing using correlation and trace identifiers
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where a requested resource is not found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex,
                                        HttpServletRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles invalid input or bad request scenarios.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException ex,
                                          HttpServletRequest request) {

        log.warn("Bad request: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles all unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex,
                                       HttpServletRequest request) {

        log.error("Unhandled exception occurred", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Internal server error",
                request.getRequestURI()
        );
    }

    /**
     * Builds a standard error response object.
     */
    private ErrorResponse buildErrorResponse(HttpStatus status,
                                             String error,
                                             String message,
                                             String path) {

        return new ErrorResponse(
                status.value(),
                error,
                message,
                path,
                MDC.get("correlationId"),
                MDC.get("traceId"),
                LocalDateTime.now()
        );
    }

    /**
     * Represents the structure of an error response.
     */
    @Data
    @AllArgsConstructor
    static class ErrorResponse {

        private int status;
        private String error;
        private String message;
        private String path;
        private String correlationId;
        private String traceId;
        private LocalDateTime timestamp;
    }
}