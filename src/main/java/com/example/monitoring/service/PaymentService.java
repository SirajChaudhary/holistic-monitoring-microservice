package com.example.monitoring.service;

import com.example.monitoring.dto.PaymentRequest;
import com.example.monitoring.dto.PaymentResponse;

/**
 * Service interface for Payment operations.
 *
 * Defines contract for business logic.
 */
public interface PaymentService {

    /**
     * Creates a new payment.
     */
    PaymentResponse createPayment(PaymentRequest request);

    /**
     * Fetch payment by ID.
     */
    PaymentResponse getPayment(Long id);
}