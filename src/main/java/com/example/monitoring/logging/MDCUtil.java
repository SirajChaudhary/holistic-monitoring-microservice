package com.example.monitoring.logging;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utility class for handling MDC (Mapped Diagnostic Context).
 *
 * MDC allows us to attach contextual information (like correlationId)
 * to every log statement automatically.
 */
public class MDCUtil {

    /**
     * Generates a unique correlation ID.
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Adds correlationId to MDC.
     */
    public static void setCorrelationId(String correlationId) {
        MDC.put(CorrelationConstants.CORRELATION_ID, correlationId);
    }

    /**
     * Clears MDC to prevent memory leaks.
     */
    public static void clear() {
        MDC.clear();
    }
}