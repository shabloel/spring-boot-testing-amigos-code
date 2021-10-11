package com.amigoscode.testing.service;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.CardPaymentCharger;
import com.amigoscode.testing.payment.Currency;
import com.amigoscode.testing.payment.PaymentRequest;
import com.amigoscode.testing.repository.CustomerRepository;
import com.amigoscode.testing.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.USD);
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    public PaymentService(CustomerRepository customerRepository,
                          PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    public void chargeCard(UUID customerId, PaymentRequest paymentRequest){

        //1. Does customer exist, if not, throw
        boolean isCustomerFound = customerRepository.findById(customerId).isPresent();

        if(!isCustomerFound){
            throw new IllegalStateException(String.format("Customer with id [%s] not found", customerId));
        }

        //2. Does currency exist, if not, throw
        boolean currencySuported = ACCEPTED_CURRENCIES.contains(paymentRequest.getPayment().getCurrency());

        if(!currencySuported){
            throw new IllegalStateException(String.format(
                    "Currency: [%s], not supported",
                    paymentRequest.getPayment().getCurrency()));
        }

        //3. charge card
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        //4 if not debited throw
        if(!cardPaymentCharge.isCardDebited()){
            throw new IllegalStateException(String.format("Card not debited for customer %s", customerId));
        }

        //5 insert payment
        paymentRequest.getPayment().setCustomerId(customerId);
        paymentRepository.save(paymentRequest.getPayment());
    }
}
