package com.example.monitoring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request DTO for creating a payment.
 */
@Data
public class PaymentRequest {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;
}