package com.autoserve.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.autoserve.dto.PaymentRequest;
import com.autoserve.dto.PaymentResponse;
import com.autoserve.entity.Payment;
import com.autoserve.enums.PaymentStatus;
import com.autoserve.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	private final PaymentRepository paymentRepository;

	

	public PaymentResponse processPayment(PaymentRequest request) {
		log.info("Processing payment for bookingId={}, amount={}", request.getBookingId(), request.getAmount());

		// Strip whitespace from card number
		String cardNumber = request.getCardNumber() == null ? "" : request.getCardNumber().replaceAll("\\s", "");

		// Validate card number format
		if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
			return buildAndSave(request, cardNumber, PaymentStatus.DECLINED);
		}

		// Validate CVV
		if (request.getCardCvv() == null || !request.getCardCvv().matches("\\d{3,4}")) {
			return buildAndSave(request, cardNumber, PaymentStatus.DECLINED);
		}

		// Validate expiry format
		if (request.getCardExpiry() == null || !request.getCardExpiry().matches("\\d{2}/\\d{2}")) {
			return buildAndSave(request, cardNumber, PaymentStatus.EXPIRED);
		}

		// Route to outcome based on test card number
		PaymentStatus outcome = resolveOutcome(cardNumber);
		log.info("Payment outcome for bookingId={}: {}", request.getBookingId(), outcome);

		return buildAndSave(request, cardNumber, outcome);
	}

	public PaymentResponse getPaymentByBookingId(Long bookingId) {
		return paymentRepository.findByBookingId(bookingId).map(PaymentResponse::from)
				.orElseThrow(() -> new RuntimeException("No payment found for booking: " + bookingId));
	}

	public List<PaymentResponse> getPaymentsByCustomer(Long customerId) {
		return paymentRepository.findByCustomerIdOrderByPaidAtDesc(customerId).stream().map(PaymentResponse::from)
				.toList();
	}

	public List<PaymentResponse> getAllPayments() {
		return paymentRepository.findAll().stream().map(PaymentResponse::from).toList();
	}

	// ── Private helpers ───────────────────────────────────────────────────────

	/**
	 * Resolves the payment outcome based on the test card number. Returns SUCCESS
	 * for any unrecognised card (teaching default).
	 */
	private PaymentStatus resolveOutcome(String cardNumber) {
		return switch (cardNumber) {
		case "4000000000000002" -> PaymentStatus.DECLINED;
		case "4000000000009995" -> PaymentStatus.INSUFFICIENT_FUNDS;
		case "4000000000000069" -> PaymentStatus.EXPIRED;
		default -> PaymentStatus.SUCCESS; // includes 4242424242424242
		};
	}

	private PaymentResponse buildAndSave(PaymentRequest request, String cardNumber, PaymentStatus status) {
		Payment payment = new Payment();
		payment.setBookingId(request.getBookingId());
		payment.setCustomerId(request.getCustomerId());
		payment.setAmount(request.getAmount());
		payment.setStatus(status);
		payment.setCardLast4(extractLast4(cardNumber));
		payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
		payment.setPaidAt(LocalDateTime.now());

		return PaymentResponse.from(paymentRepository.save(payment));
	}

	private String extractLast4(String cardNumber) {
		if (cardNumber == null || cardNumber.length() < 4)
			return "0000";
		return cardNumber.substring(cardNumber.length() - 4);
	}
}