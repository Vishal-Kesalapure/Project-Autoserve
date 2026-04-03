package com.autoserve.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoserve.dto.CreateAdminRequest;
import com.autoserve.dto.MechanicResponse;
import com.autoserve.dto.VehicleOwnerResponse;
import com.autoserve.entity.Admin;
import com.autoserve.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/customers")
	public ResponseEntity<?> listCustomers() {
		List<VehicleOwnerResponse> list = userService.findAllOwners().stream().map(VehicleOwnerResponse::from).toList();
		return ResponseEntity.ok(list);
	}

	@PutMapping("/customers/{id}/disable")
	public ResponseEntity<?> disableCustomer(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(VehicleOwnerResponse.from(userService.disableOwner(id)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/customers/{id}/enable")
	public ResponseEntity<?> enableCustomer(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(VehicleOwnerResponse.from(userService.enableOwner(id)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/customers/{id}")
	public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
		try {
			userService.deleteOwner(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/mechanics")
	public ResponseEntity<?> listMechanics() {
		List<MechanicResponse> list = userService.findAllMechanics().stream().map(MechanicResponse::from).toList();
		return ResponseEntity.ok(list);
	}

	@PutMapping("/mechanics/{id}/verify")
	public ResponseEntity<?> verifyMechanic(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(MechanicResponse.from(userService.verifyMechanic(id)));
		} catch (IllegalStateException e) {
			// FIX: re-verify guard — return 409 Conflict instead of silent no-op
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/mechanics/{id}/disable")
	public ResponseEntity<?> disableMechanic(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(MechanicResponse.from(userService.disableMechanic(id)));
		} catch (jakarta.persistence.EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body(Map.of("error", "Mechanic disabled in user-service but garage deactivation failed: "
							+ e.getMessage()));
		}
	}

	
	@PutMapping("/mechanics/{id}/enable")
	public ResponseEntity<?> enableMechanic(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(MechanicResponse.from(userService.enableMechanic(id)));
		} catch (jakarta.persistence.EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			// FIX: Feign failure propagates — inform admin the garage activation failed
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body(Map.of("error", "Mechanic enabled in user-service but garage activation failed: "
							+ e.getMessage()));
		}
	}

	@DeleteMapping("/mechanics/{id}")
	public ResponseEntity<?> deleteMechanic(@PathVariable Long id) {
		try {
			userService.deleteMechanic(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/admins")
	public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
		if (userService.existsByUsername(request.username())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
		}
		Admin admin = new Admin();
		admin.setUsername(request.username());
		admin.setPassword(passwordEncoder.encode(request.password()));
		admin.setFirstName(request.firstName());
		admin.setLastName(request.lastName());
		admin.setMobileNumber(request.mobileNumber());
		admin.setAccountEnabled(true);
		Admin saved = userService.saveAdmin(admin);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("id", saved.getId(), "username", saved.getUsername(), "role", "ADMIN"));
	}

	@GetMapping("/admins")
	public ResponseEntity<?> listAdmins() {
		List<Map<String, Object>> list = userService.findAllAdmins().stream().map(a -> Map.of("id", (Object) a.getId(),
				"username", a.getUsername(), "firstName", a.getFirstName(), "lastName", a.getLastName())).toList();
		return ResponseEntity.ok(list);
	}
}
