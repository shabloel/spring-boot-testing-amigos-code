package com.amigoscode.testing.payment;

import com.stripe.net.RequestOptions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public interface CardPaymentCharger {


    CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description);

}
