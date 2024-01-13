package com.sachet.paymentserviceamazon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCancelledEventModal {
    private String orderId;
    private String itemId;
}
