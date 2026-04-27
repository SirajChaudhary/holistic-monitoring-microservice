package com.example.monitoring.dto;

import com.example.monitoring.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Response DTO for payment API.
 */
@Data
@Builder
public class PaymentResponse {

    private String referenceId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
}