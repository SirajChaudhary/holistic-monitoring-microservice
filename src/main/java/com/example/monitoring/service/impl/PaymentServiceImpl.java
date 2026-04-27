package com.example.monitoring.service.impl;

import com.example.monitoring.dto.PaymentRequest;
import com.example.monitoring.dto.PaymentResponse;
import com.example.monitoring.model.Payment;
import com.example.monitoring.model.PaymentStatus;
import com.example.monitoring.repository.PaymentRepository;
import com.example.monitoring.service.PaymentService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of PaymentService.
 *
 * 🔥 THIS IS THE CORE OBSERVABILITY CLASS 🔥
 *
 * Responsibilities:
 * - Business logic execution
 * - Logging (structured + contextual)
 * - Tracing (Micrometer Observation → Zipkin)
 * - Metrics (Micrometer)
 * - Error handling & monitoring
 *
 * Why Micrometer Observation API?
 *
 * Micrometer does NOT auto-create spans for:
 * - controller methods
 * - service methods
 *
 * It only creates:
 * - HTTP span
 * - DB span (if enabled)
 * - messaging spans
 *
 * As a result, controller/service-level execution is not visible in Zipkin by default.
 *
 * 👉 So we use Observation API to manually create spans for business logic and visualize them in Zipkin.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    // MeterRegistry → used to collect custom metrics (counters, timers, etc.)
    private final MeterRegistry meterRegistry;

    // ObservationRegistry → Micrometer Observation API used to create spans and send them to Zipkin
    private final ObservationRegistry observationRegistry;

    /**
     * Creates a payment with full observability.
     *
     * Observability included:
     * - Logs (INFO, ERROR)
     * - Custom span via Observation (visible in Zipkin)
     * - Metrics (success, failure, execution time)
     */
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        // TRACING: Create custom span using Micrometer Observation API (visible in Zipkin)
        return Observation.createNotStarted("service.createPayment", observationRegistry)
                .observe(() -> {

                    // METRICS: Start timer (Used to track performance)
                    Timer.Sample sample = Timer.start(meterRegistry);

                    try {
                        // LOGGING: correlationId, traceId, spanId will automatically be included
                        log.info("Starting payment processing: amount={}, currency={}",
                                request.getAmount(), request.getCurrency());

                        // BUSINESS LOGIC: Simulate failure scenario
                        if (request.getAmount().doubleValue() > 10000) {
                            throw new RuntimeException("Amount exceeds limit!");
                        }

                        // BUSINESS LOGIC: Create payment entity
                        Payment payment = Payment.builder()
                                .referenceId(UUID.randomUUID().toString())
                                .amount(request.getAmount())
                                .currency(request.getCurrency())
                                .status(PaymentStatus.SUCCESS)
                                .createdAt(LocalDateTime.now())
                                .build();

                        // BUSINESS LOGIC: Persist to database

                        // DB OPERATION (TRACING + METRICS)
                        Timer.Sample dbSample = Timer.start(meterRegistry); // METRICS: start DB latency tracking

                        try {
                            // TRACING: DB operation span (visible in Zipkin)
                            Observation.createNotStarted("db.save.payment", observationRegistry)
                                    .observe(() -> paymentRepository.save(payment));

                        } finally {
                            /**
                             * METRICS:
                             * Used to show DB performance in Grafana
                             * Flow: Actuator → Prometheus → Grafana
                             */
                            dbSample.stop(
                                    Timer.builder("db.operation.time")
                                            .tag("operation", "save")
                                            .tag("entity", "payment")
                                            .register(meterRegistry)
                            );
                        }

                        // METRICS: Success counter
                        meterRegistry.counter("payment_success_total").increment();

                        log.info("Payment created successfully: referenceId={}",
                                payment.getReferenceId());

                        // RETURN RESPONSE
                        return PaymentResponse.builder()
                                .referenceId(payment.getReferenceId())
                                .amount(payment.getAmount())
                                .currency(payment.getCurrency())
                                .status(payment.getStatus())
                                .build();

                    } catch (Exception ex) {

                        // METRICS: Failure counter
                        meterRegistry.counter("payment_failure_total").increment();

                        // LOGGING: Error log with full context (traceId, correlationId)
                        log.error("Payment processing failed", ex);

                        throw ex;

                    } finally {

                        // METRICS: Record execution time
                        sample.stop(
                                Timer.builder("payment_processing_time")
                                        .description("Time taken to process payment")
                                        .register(meterRegistry)
                        );
                    }
                });
    }

    /**
     * Fetch payment by ID with observability.
     *
     * Observability included:
     * - Logs
     * - Custom span via Observation
     */
    @Override
    public PaymentResponse getPayment(Long id) {

        // TRACING: Create span for fetch operation
        return Observation.createNotStarted("service.getPayment", observationRegistry)
                .observe(() -> {

                    // LOGGING: correlationId, traceId automatically included
                    log.info("Fetching payment with id={}", id);

                    // BUSINESS LOGIC: Fetch from database
                    Payment payment = paymentRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Payment not found"));

                    // RETURN RESPONSE
                    return PaymentResponse.builder()
                            .referenceId(payment.getReferenceId())
                            .amount(payment.getAmount())
                            .currency(payment.getCurrency())
                            .status(payment.getStatus())
                            .build();
                });
    }
}