package com.sachet.paymentserviceamazon.repository;

import com.sachet.paymentserviceamazon.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
}
