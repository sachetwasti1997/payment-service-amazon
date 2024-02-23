package com.sachet.paymentserviceamazon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sachet.paymentserviceamazon.model.*;
import com.sachet.paymentserviceamazon.publisher.PaymentStatusPublisher;
import com.sachet.paymentserviceamazon.repository.OrderRepository;
import com.sachet.paymentserviceamazon.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Token;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    private final PaymentStatusPublisher paymentStatusPublisher;
    private final String stripeKey;

    public PaymentService(OrderRepository orderRepository,
                          @Value("${STRIPE_SECURE_KEY}") String stripeKey,
                          PaymentRepository paymentRepository,
                          PaymentStatusPublisher paymentStatusPublisher) {
        this.orderRepository = orderRepository;
        this.stripeKey = stripeKey;
        this.paymentRepository = paymentRepository;
        this.paymentStatusPublisher = paymentStatusPublisher;
    }

    public PaymentIntent createPaymentIntent(Payment payment) throws Exception {
        Stripe.apiKey = stripeKey;
        Optional<OrderCreated> orderCreated = orderRepository.findById(payment.getOrderId());
        if (orderCreated.isEmpty()) {
            throw new Exception("No Order Found");
        }
        OrderCreated actualOrder = orderCreated.get();
        if (actualOrder.getStatus().equals("ORDER_EXPIRED") ||
        actualOrder.getStatus().equals("ORDER_CANCELLED")||
        actualOrder.getStatus().equals("PAYMENT_COMPLETE")) {
            throw new Exception("Cannot complete payment");
        }
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", actualOrder.getOrderPrice().intValue());
        params.put("currency", "USD");
        params.put("payment_method_types", paymentMethodTypes);

        paymentRepository.save(payment);

        return PaymentIntent.create(params);
    }

    public String paymentComplete(PaymentStatus paymentStatus) throws JsonProcessingException {
        Optional<Payment> getPayment = paymentRepository.findByOrderId(paymentStatus.getOrderId());
        var payment = getPayment.orElseThrow();
        payment.setPaymentStatus(paymentStatus.getPaymentStatus());
        paymentStatusPublisher.publishPaymentStatus(paymentStatus);
        paymentRepository.save(payment);
        return paymentStatus.getPaymentStatus();
    }

}
