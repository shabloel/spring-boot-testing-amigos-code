package com.amigoscode.testing.repository;

import com.amigoscode.testing.payment.Currency;
import com.amigoscode.testing.payment.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class PaymentRepositoryTest {

    private PaymentRepository paymentRepository;

    @Autowired
    public PaymentRepositoryTest(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Test
    void itShouldInsertPayment() {
        // given
        Long paymentId = 1L;
        Payment payment = Payment.builder()
                            .customerId(UUID.randomUUID())
                            .amount(new BigDecimal("10.0"))
                            .currency(Currency.USD)
                            .source("DebitCard")
                            .description("Payment for fun")
                            .build();
        // when
        paymentRepository.save(payment);

        // then
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        assertThat(paymentOptional)
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p).isEqualToComparingFieldByField(payment);
                });
    }
}