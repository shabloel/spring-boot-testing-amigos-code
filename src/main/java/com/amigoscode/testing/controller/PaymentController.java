package com.amigoscode.testing.controller;

import com.amigoscode.testing.payment.PaymentRequest;
import com.amigoscode.testing.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment-registration")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public void registerNewPayment(@RequestBody PaymentRequest request){
        paymentService.chargeCard(request.getPayment().getCustomerId(), request);
    }
}
