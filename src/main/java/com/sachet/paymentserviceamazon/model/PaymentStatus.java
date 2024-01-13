package com.sachet.paymentserviceamazon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document
public class PaymentStatus {
    private String orderId;
    @Id
    private String paymentId;
    private String paymentStatus;
}
