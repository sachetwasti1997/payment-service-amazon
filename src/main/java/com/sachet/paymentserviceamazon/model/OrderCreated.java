package com.sachet.paymentserviceamazon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreated {
    @Id
    private String orderId;
    private String status;
    private String expiresAt;
    private String itemId;
    private Integer orderQuantity;
    private Double orderPrice;
    private String userEmail;
}
