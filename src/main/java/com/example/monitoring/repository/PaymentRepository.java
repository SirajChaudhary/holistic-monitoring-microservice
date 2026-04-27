package com.example.monitoring.repository;

import com.example.monitoring.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for Payment entity.
 *
 * Provides CRUD operations via Spring Data JPA.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReferenceId(String referenceId);
}