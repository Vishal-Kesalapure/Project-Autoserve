package com.autoserve.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.autoserve.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long bookingId;

	@Column(nullable = false)
	private Long customerId;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	/** Last 4 digits of card — safe to persist */
	private String cardLast4;

	/** Unique transaction reference (mock UUID) */
	@Column(unique = true)
	private String transactionId;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

}
