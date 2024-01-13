package com.sachet.paymentserviceamazon.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StripeCharge {

    private String stripeToken;
    private String userName;
    private Double amount;
    private boolean success;
    private String message;
    private String chargeId;
    private Map<String, Object> additionalInfo = new HashMap<>();

}
