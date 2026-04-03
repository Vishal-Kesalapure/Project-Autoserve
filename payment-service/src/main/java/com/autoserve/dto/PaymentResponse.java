package com.autoserve.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.autoserve.entity.Payment;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PaymentResponse {

	private Long id;
	private Long bookingId;
	private Long customerId;
	private BigDecimal amount;
	private String status; // SUCCESS | DECLINED | INSUFFICIENT_FUNDS | EXPIRED
	private String cardLast4;
	private String transactionId;
	private LocalDateTime paidAt;



	public static PaymentResponse from(Payment p) {
		PaymentResponse r = new PaymentResponse();
		r.setId(p.getId());
		r.setBookingId(p.getBookingId());
		r.setCustomerId(p.getCustomerId());
		r.setAmount(p.getAmount());
		r.setStatus(p.getStatus().name());
		r.setCardLast4(p.getCardLast4());
		r.setTransactionId(p.getTransactionId());
		r.setPaidAt(p.getPaidAt());
		return r;
	}
}