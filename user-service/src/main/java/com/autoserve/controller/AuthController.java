package com.autoserve.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoserve.dto.AuthResponse;
import com.autoserve.dto.LoginRequest;
import com.autoserve.dto.RegisterRequest;
import com.autoserve.entity.Mechanic;
import com.autoserve.entity.User;
import com.autoserve.entity.VehicleOwner;
import com.autoserve.enums.FuelType;
import com.autoserve.security.JwtService;
import com.autoserve.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

		if (userService.existsByUsername(request.username())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
		}

		return switch (request.role().toUpperCase()) {
		case "CUSTOMER" -> registerOwner(request);
		case "SUPPLIER" -> registerMechanic(request);
		default -> ResponseEntity.badRequest().body(Map.of("error", "Invalid role. Use CUSTOMER or SUPPLIER"));
		};
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

		var user = userService.loadUserByUsername(request.username());
		var casted = (User) user;

		// Backfill safety for old supplier accounts created before strict garage flow.
		if (casted instanceof Mechanic mechanic) {
			try {
				userService.ensureGarageProfile(mechanic);
			} catch (IllegalStateException e) {
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
						.body(Map.of("error", "Login succeeded, but garage profile could not be prepared. Please retry."));
			}
		}

		String token = jwtService.generateToken(casted);
		return ResponseEntity.ok(new AuthResponse(token, casted.getId(), casted.getRole().name()));
	}

	private ResponseEntity<?> registerOwner(RegisterRequest req) {
		if (req.vehicleNumber() == null || req.vehicleNumber().isBlank() || req.vehicleMake() == null
				|| req.vehicleMake().isBlank() || req.vehicleModel() == null || req.vehicleModel().isBlank()
				|| req.vehicleYear() == null || req.fuelType() == null || req.fuelType().isBlank()) {
			return ResponseEntity.badRequest().body(Map.of("error",
					"CUSTOMER registration requires vehicleNumber, vehicleMake, vehicleModel, vehicleYear, fuelType"));
		}

		FuelType fuelType;
		try {
			fuelType = FuelType.valueOf(req.fuelType().toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "Invalid fuelType. Use PETROL, DIESEL, ELECTRIC or CNG"));
		}

		VehicleOwner owner = new VehicleOwner();
		owner.setUsername(req.username());
		owner.setPassword(passwordEncoder.encode(req.password()));
		owner.setFirstName(req.firstName());
		owner.setLastName(req.lastName());
		owner.setMobileNumber(req.mobileNumber());
		owner.setVehicleNumber(req.vehicleNumber());
		owner.setVehicleMake(req.vehicleMake());
		owner.setVehicleModel(req.vehicleModel());
		owner.setVehicleYear(req.vehicleYear());
		owner.setFuelType(fuelType);

		VehicleOwner saved = userService.saveOwner(owner);
		String token = jwtService.generateToken(saved);
		return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, saved.getId(), "CUSTOMER"));
	}

	private ResponseEntity<?> registerMechanic(RegisterRequest req) {
		if (req.garageName() == null || req.garageName().isBlank() || req.garageAddress() == null
				|| req.garageAddress().isBlank()) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "SUPPLIER registration requires garageName and garageAddress"));
		}

		Mechanic mechanic = new Mechanic();
		mechanic.setUsername(req.username());
		mechanic.setPassword(passwordEncoder.encode(req.password()));
		mechanic.setFirstName(req.firstName());
		mechanic.setLastName(req.lastName());
		mechanic.setMobileNumber(req.mobileNumber());
		mechanic.setGarageName(req.garageName());
		mechanic.setGarageAddress(req.garageAddress());
		mechanic.setSpecializations(req.specializations());
		mechanic.setCertifications(req.certifications());
		mechanic.setExperienceYears(req.experienceYears());
		mechanic.setVerified(false);
		mechanic.setRating(0.0);

		try {
			Mechanic saved = userService.saveMechanic(mechanic);
			String token = jwtService.generateToken(saved);
			return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, saved.getId(), "SUPPLIER"));
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body(Map.of("error", e.getMessage()));
		
		}
	}
}
