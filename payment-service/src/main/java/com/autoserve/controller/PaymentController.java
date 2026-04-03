package com.autoserve.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoserve.dto.PaymentRequest;
import com.autoserve.dto.PaymentResponse;
import com.autoserve.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	
	@PostMapping("/process")
	public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
		PaymentResponse response = paymentService.processPayment(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/my")
	public ResponseEntity<List<PaymentResponse>> getMyPayments(@RequestHeader("X-User-Id") Long userId) {
		return ResponseEntity.ok(paymentService.getPaymentsByCustomer(userId));
	}

	
	@GetMapping("/booking/{bookingId}")
	public ResponseEntity<?> getPaymentByBooking(@RequestHeader("X-User-Id") Long userId,
			@RequestHeader(value = "X-User-Role", defaultValue = "") String role, @PathVariable Long bookingId) {
		try {
			PaymentResponse payment = paymentService.getPaymentByBookingId(bookingId);

			// Admins see any; customers see only their own payments
			boolean isAdmin = "ROLE_ADMIN".equals(role);
			boolean isOwner = payment.getCustomerId().equals(userId);

			if (!isAdmin && !isOwner) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
			}

			return ResponseEntity.ok(payment);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllPayments(@RequestHeader(value = "X-User-Role", defaultValue = "") String role) {

		if (!"ROLE_ADMIN".equals(role)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Admin access required"));
		}

		return ResponseEntity.ok(paymentService.getAllPayments());
	}
}