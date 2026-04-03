package com.autoserve.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Sent from booking-service (via Feign) when a customer pays their bill.
 * Card details are passed through and only the last 4 digits are stored.
 */
@Data
@Getter
@Setter

public class PaymentRequest {

    private Long bookingId;
    private Long customerId;
    private BigDecimal amount;

    /** 16-digit card number, no spaces — e.g. "4242424242424242" */
    private String cardNumber;

    /** MM/YY format — e.g. "12/26" */
    private String cardExpiry;

    /** 3-digit CVV */
    private String cardCvv;

    private String cardHolderName;
}