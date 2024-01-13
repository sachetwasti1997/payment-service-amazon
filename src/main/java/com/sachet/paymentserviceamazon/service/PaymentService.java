package com.sachet.paymentserviceamazon.service;

import com.sachet.paymentserviceamazon.model.OrderCreated;
import com.sachet.paymentserviceamazon.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderCreated> getAllOrders() {
        return orderRepository.findAll();
    }
}
