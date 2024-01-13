package com.sachet.paymentserviceamazon.controller;

import com.sachet.paymentserviceamazon.model.OrderCreated;
import com.sachet.paymentserviceamazon.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/orders/test/all")
    public ResponseEntity<List<OrderCreated>> getAllOrders() {
        return ResponseEntity.ok(paymentService.getAllOrders());
    }
}
