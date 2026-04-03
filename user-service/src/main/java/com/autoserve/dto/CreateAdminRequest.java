package com.autoserve.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAdminRequest(@NotBlank String username, @NotBlank String password, @NotBlank String firstName,
		@NotBlank String lastName, @NotBlank String mobileNumber) {
}