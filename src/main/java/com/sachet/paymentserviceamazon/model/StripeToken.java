package com.sachet.paymentserviceamazon.model;

import lombok.Data;

@Data
public class StripeToken {

    private String expireMonth;
    private String expYear;
    private String token;
    private String cvc;
    private String cardNumber;
    private String userName;
    private boolean success;

}
