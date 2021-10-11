package com.amigoscode.testing.repository;

import com.amigoscode.testing.payment.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
