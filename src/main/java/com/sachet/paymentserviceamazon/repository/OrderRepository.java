package com.sachet.paymentserviceamazon.repository;

import com.sachet.paymentserviceamazon.model.OrderCreated;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<OrderCreated, String> {
}
