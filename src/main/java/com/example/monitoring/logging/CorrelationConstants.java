package com.example.monitoring.logging;

/**
 * Constants used for logging correlation.
 *
 * These keys are used in MDC (Mapped Diagnostic Context)
 * and will appear in every log statement.
 */
public class CorrelationConstants {

    public static final String CORRELATION_ID = "correlationId";
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";

    private CorrelationConstants() {
        // Prevent instantiation
    }
}