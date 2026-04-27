package com.example.monitoring.logging;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter intercepts every HTTP request.
 *
 * Responsibilities:
 * 1. Read correlationId from request header (optional)
 * 2. Generate new correlationId if not provided
 * 3. Add correlationId to MDC for log correlation
 * 4. Extract traceId and spanId from OpenTelemetry
 * 5. Add correlationId and traceId to response headers
 * 6. Log incoming request and outgoing response
 * 7. Log errors with correlationId and traceId
 * 8. Clear MDC after request completion
 *
 * If user does not provide X-Correlation-Id, a new correlationId is generated.
 *
 * This is the backbone of logging and tracing in production systems.
 */
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // 1. Read correlationId from header (optional) or generate new
        String correlationId = request.getHeader(HEADER_CORRELATION_ID);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = MDCUtil.generateCorrelationId();
        }

        // 2. Add correlationId to MDC
        MDCUtil.setCorrelationId(correlationId);

        // 3. Add correlationId to response header
        response.setHeader(HEADER_CORRELATION_ID, correlationId);

        try {
            // 4. Extract traceId and spanId from OpenTelemetry
            Span currentSpan = Span.current();
            SpanContext spanContext = currentSpan.getSpanContext();

            String traceId = spanContext.getTraceId();
            String spanId = spanContext.getSpanId();

            if (traceId != null && !"00000000000000000000000000000000".equals(traceId)) {
                MDC.put(CorrelationConstants.TRACE_ID, traceId);
                MDC.put(CorrelationConstants.SPAN_ID, spanId);

                // Add traceId to response header
                response.setHeader(HEADER_TRACE_ID, traceId);
            }

            // 5. Log incoming request
            log.info("Incoming Request: method={}, uri={}, correlationId={}, traceId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    correlationId,
                    MDC.get(CorrelationConstants.TRACE_ID));

            // Continue request processing
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // 6. Log error with correlationId and traceId
            log.error("Error occurred: correlationId={}, traceId={}",
                    correlationId,
                    MDC.get(CorrelationConstants.TRACE_ID),
                    ex);
            throw ex;

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 7. Log outgoing response
            log.info("Outgoing Response: status={}, duration={}ms, correlationId={}, traceId={}",
                    response.getStatus(),
                    duration,
                    correlationId,
                    MDC.get(CorrelationConstants.TRACE_ID));

            // 8. Clear MDC
            MDCUtil.clear();
        }
    }
}