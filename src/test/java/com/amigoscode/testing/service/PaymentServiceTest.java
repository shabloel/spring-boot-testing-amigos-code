package com.amigoscode.testing.service;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.payment.*;
import com.amigoscode.testing.repository.CustomerRepository;
import com.amigoscode.testing.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository,
                                        paymentRepository,
                                        cardPaymentCharger);
    }

    @Test
    void it_Should_Throw_If_Customer_Not_Exists() {
        // given
        UUID uuid = UUID.randomUUID();

        given(customerRepository.findById(uuid)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.chargeCard(uuid, any()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer with id");

        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void it_should_charge_card_succesfully() {
        // given
        UUID uuid = UUID.randomUUID();
        given(customerRepository.findById(uuid)).willReturn(Optional.of(mock(Customer.class)));
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("10.0"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(true));
        // when
        underTest.chargeCard(uuid, paymentRequest);
        // then
        ArgumentCaptor<Payment> paymentArgumentCaptor =
                ArgumentCaptor.forClass(Payment.class);

        //verify if repo is called
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment payment = paymentArgumentCaptor.getValue();

        assertThat(payment).isEqualToIgnoringGivenFields(paymentRequest.getPayment(),
                "uuid");

        assertThat(payment.getCustomerId()).isEqualTo(uuid);
    }

    @Test
    void it_should_throw_when_card_not_debited() {
        // given
        UUID uuid = UUID.randomUUID();
        given(customerRepository.findById(uuid)).willReturn(Optional.of(mock(Customer.class)));
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("10.0"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(false));


        assertThatThrownBy(() -> underTest.chargeCard(uuid, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card not debited");

        //verify if repo is called
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void it_should_throw_when_currency_not_supported() {
        // given
        UUID uuid = UUID.randomUUID();
        given(customerRepository.findById(uuid)).willReturn(Optional.of(mock(Customer.class)));
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("10.0"),
                        Currency.GBP,
                        "card123xx",
                        "Donation"
                )
        );

        //when
        assertThatThrownBy(() -> underTest.chargeCard(uuid, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Currency:");

        //then
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}