package com.autoserve.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Single DTO for both Vehicle Owner (CUSTOMER) and Mechanic (SUPPLIER)
 * self-registration. Role-specific fields are nullable -- validated in the
 * service layer.
 */
public record RegisterRequest(

		@NotBlank String username, @NotBlank String password, @NotBlank String firstName, @NotBlank String lastName,
		@NotBlank String mobileNumber,

		@NotBlank String role,

		String vehicleNumber, String vehicleMake, String vehicleModel, Integer vehicleYear, String fuelType,
		String garageName, String garageAddress, String specializations, String certifications,
		Integer experienceYears) {
}