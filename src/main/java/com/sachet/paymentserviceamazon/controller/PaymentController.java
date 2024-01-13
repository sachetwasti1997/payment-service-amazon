package com.sachet.paymentserviceamazon.controller;

import com.sachet.paymentserviceamazon.model.OrderCreated;
import com.sachet.paymentserviceamazon.model.Payment;
import com.sachet.paymentserviceamazon.model.StripeCharge;
import com.sachet.paymentserviceamazon.model.StripeToken;
import com.sachet.paymentserviceamazon.service.PaymentService;
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

    @PostMapping("/payment/validate")
    public ResponseEntity<StripeToken> validateCard(@RequestBody StripeToken stripeToken) throws Exception {
        return ResponseEntity.ok(paymentService.createCardToken(stripeToken));
    }

    @PostMapping("/payment/pay")
    public ResponseEntity<Payment> pay(@RequestBody Payment payment) throws Exception {
        return ResponseEntity.ok(paymentService.processPayment(payment));
    }

    @GetMapping("/orders/test/all")
    public ResponseEntity<List<OrderCreated>> getAllOrders() {
        return ResponseEntity.ok(paymentService.getAllOrders());
    }
}
