package com.sachet.paymentserviceamazon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sachet.paymentserviceamazon.model.*;
import com.sachet.paymentserviceamazon.publisher.PaymentStatusPublisher;
import com.sachet.paymentserviceamazon.repository.OrderRepository;
import com.sachet.paymentserviceamazon.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeKey;
    }

    public StripeToken createCardToken(StripeToken modal) throws Exception {
        try {
            Map<String, Object> card = new HashMap<>();
            card.put("number", modal.getCardNumber());
            card.put("exp_year", Integer.parseInt(modal.getExpYear()));
            card.put("exp_month", Integer.parseInt(modal.getExpireMonth()));
            card.put("cvc", modal.getCvc());
            Map<String, Object> params = new HashMap<>();
            params.put("card", card);
            Token token = Token.create(params);
            if (token != null && token.getId() != null) {
                modal.setToken(token.getId());
                modal.setSuccess(true);
            }
            return modal;
        }catch (StripeException ex){
            LOGGER.error("Error creating the token {}", ex.getMessage());
            throw new Exception("Error while creating the token");
        }
    }

    public StripeCharge charge(StripeCharge chargeRequest) throws Exception {
        try{
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", chargeRequest.getAmount() * 100);
            chargeParams.put("currency", "USD");
            chargeParams.put("description", "Payment for id "+chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
            chargeParams.put("source", chargeRequest.getStripeToken());

            Map<String, Object> metaData = new HashMap<>();
            metaData.put("id", chargeRequest.getChargeId());
            metaData.putAll(chargeRequest.getAdditionalInfo());
            chargeParams.put("metaData", metaData);

            Charge charge = Charge.create(chargeParams);
            chargeRequest.setMessage(charge.getOutcome().getSellerMessage());

            if (charge.getPaid()) {
                chargeRequest.setChargeId(charge.getId());
                chargeRequest.setSuccess(true);
            }
            return chargeRequest;
        }catch (StripeException ex) {
            LOGGER.error("Error while charging {}", ex.getMessage());
            throw new Exception("Error while charging "+ex.getMessage());
        }
    }

    public Payment processPayment(Payment payment) throws Exception {
        Optional<OrderCreated> orderCreated = orderRepository.findById(payment.getOrderId());
        if (orderCreated.isEmpty()) {
            throw new Exception("Cannot complete the payment, Order is either expired or cancelled");
        }
        OrderCreated saved = orderCreated.get();
        if (saved.getStatus().equals("ORDER_EXPIRED")||
                saved.getStatus().equals("PAYMENT_COMPLETE")){
            throw new Exception("Cannot complete the payment, Order is either expired or already complete");
        }
        payment = paymentRepository.save(payment);
        PaymentStatus paymentStatus = new PaymentStatus(payment.getOrderId(), payment.getPaymentId(), "PAYMENT_COMPLETE");
        saved.setStatus("PAYMENT_COMPLETE");
        orderRepository.save(saved);
        paymentStatusPublisher.publishPaymentStatus(paymentStatus);
        return payment;
    }

    public List<OrderCreated> getAllOrders() {
        return orderRepository.findAll();
    }
}
