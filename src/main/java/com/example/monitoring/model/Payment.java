package com.example.monitoring.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a payment transaction.
 *
 * This will be stored in PostgreSQL.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique payment reference
     */
    @Column(nullable = false, unique = true)
    private String referenceId;

    /**
     * Payment amount
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Currency (e.g., INR, USD)
     */
    @Column(nullable = false)
    private String currency;

    /**
     * Payment status
     */
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    /**
     * Timestamp of creation
     */
    private LocalDateTime createdAt;
}