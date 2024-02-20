package com.sachet.paymentserviceamazon.controller;

import com.sachet.paymentserviceamazon.model.OrderCreated;
import com.sachet.paymentserviceamazon.model.Payment;
import com.sachet.paymentserviceamazon.model.StripeCharge;
import com.sachet.paymentserviceamazon.model.StripeToken;
import com.sachet.paymentserviceamazon.service.PaymentService;
import com.stripe.model.PaymentIntent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<String> doPayment(
            @RequestBody Payment payment
    ) throws Exception {
        return ResponseEntity.ok(paymentService.createPaymentIntent(payment).toJson());
    }
}
