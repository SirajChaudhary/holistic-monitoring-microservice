package com.example.monitoring.controller;

import com.example.monitoring.dto.PaymentRequest;
import com.example.monitoring.dto.PaymentResponse;
import com.example.monitoring.service.PaymentService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Payment APIs.
 *
 * Responsibilities:
 * - Accept incoming HTTP requests
 * - Delegate to service layer
 * - Trigger observability (logs, traces, metrics)
 *
 * Observability Notes:
 * - Logging includes correlationId and traceId automatically (via MDC)
 * - You can trace a complete request using traceId and visualize it in Zipkin
 * - By default, Micrometer ONLY creates:
 *     - HTTP span
 *     - DB span (if enabled)
 *     - messaging spans
 *
 * - Micrometer does NOT create spans for:
 *     - controller methods
 *     - service methods
 *
 * 👉 So controller/service execution is NOT visible in Zipkin by default
 *
 * 👉 We use Observation API to manually create spans so we can see full flow in Zipkin:
 *
 *    HTTP → Controller → Service → DB
 *
 * All requests pass through LoggingFilter before reaching this controller.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ObservationRegistry → used to create custom spans for controller layer (visible in Zipkin)
    private final ObservationRegistry observationRegistry;

    /**
     * Create a new payment.
     *
     * Observability:
     * - LOGGING: correlationId, traceId automatically included
     * - TRACING: HTTP span + controller span + service span
     * - METRICS: handled in service layer
     *
     * 👉 traceId can be used to search logs in Kibana and traces in Zipkin
     */
    @PostMapping
    public PaymentResponse createPayment(@Valid @RequestBody PaymentRequest request) {

        // TRACING: controller-level span (visible in Zipkin)
        return Observation.createNotStarted("controller.createPayment", observationRegistry)
                .observe(() -> {

                    // LOGGING: request entry log (includes correlationId, traceId)
                    log.info("Received create payment request");

                    // BUSINESS LOGIC: delegate to service
                    return paymentService.createPayment(request);
                });
    }

    /**
     * Fetch payment by ID.
     *
     * 👉 traceId helps correlate logs and traces for this request
     */
    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable("id") Long id) {

        // TRACING: controller-level span
        return Observation.createNotStarted("controller.getPayment", observationRegistry)
                .observe(() -> {

                    // LOGGING
                    log.info("Received get payment request for id={}", id);

                    return paymentService.getPayment(id);
                });
    }

    /**
     * Simulate an error.
     *
     * Observability:
     * - Span will be marked as ERROR in Zipkin
     * - traceId helps identify failure in logs and traces
     */
    @GetMapping("/simulate-error")
    public String simulateError() {

        return Observation.createNotStarted("controller.simulateError", observationRegistry)
                .observe(() -> {

                    log.info("Simulating error endpoint");

                    throw new RuntimeException("Simulated exception for monitoring");
                });
    }

    /**
     * Simulate external call.
     *
     * Observability:
     * - Helps visualize latency in Zipkin timeline
     * - traceId allows tracking this delay across logs and traces
     */
    @GetMapping("/external-call")
    public String externalCall() {

        return Observation.createNotStarted("controller.externalCall", observationRegistry)
                .observe(() -> {

                    log.info("Simulating external service call");

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return "External call completed";
                });
    }
}