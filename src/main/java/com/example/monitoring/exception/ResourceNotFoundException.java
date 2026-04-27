package com.example.monitoring.exception;

/**
 * Exception thrown when a requested resource is not found.
 *
 * Example:
 * - Payment not found
 * - User not found
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor with message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}