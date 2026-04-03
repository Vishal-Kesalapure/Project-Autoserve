package com.autoserve.controller;

import com.autoserve.dto.*;
import com.autoserve.service.GarageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/garages")
@RequiredArgsConstructor
public class GarageController {

    private final GarageService garageService;

    @GetMapping
    public ResponseEntity<List<GarageResponse>> getAllGarages() {
        return ResponseEntity.ok(garageService.getAllActiveGarages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGarageById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(garageService.getGarageById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<GarageResponse>> searchGarages(
            @RequestParam(value = "q", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(garageService.searchGarages(query));
    }

    @GetMapping("/specialization/{spec}")
    public ResponseEntity<List<GarageResponse>> getBySpecialization(@PathVariable String spec) {
        return ResponseEntity.ok(garageService.getBySpecialization(spec));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerGarage(@Valid @RequestBody RegisterGarageRequest request) {
        try {
            GarageResponse response = garageService.registerGarage(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/internal/{id}")
    public ResponseEntity<?> getGarageInternal(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(garageService.getGarageByIdInternal(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<?> getServiceOffering(@PathVariable Long serviceId) {
        try {
            return ResponseEntity.ok(garageService.getServiceOfferingById(serviceId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllGaragesAdmin(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        if (!isAdmin(role)) return forbidden();
        return ResponseEntity.ok(garageService.getAllGaragesForAdmin());
    }

    @PutMapping("/admin/mechanic/{mechanicId}/deactivate")
    public ResponseEntity<?> deactivateGarage(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @PathVariable Long mechanicId) {
        if (!isAdmin(role)) return forbidden();
        try {
            garageService.deactivateGarageByMechanicId(mechanicId);
            return ResponseEntity.ok(Map.of("message", "Garage deactivated for mechanic: " + mechanicId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/mechanic/{mechanicId}/activate")
    public ResponseEntity<?> activateGarage(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @PathVariable Long mechanicId) {
        if (!isAdmin(role)) return forbidden();
        try {
            garageService.activateGarageByMechanicId(mechanicId);
            return ResponseEntity.ok(Map.of("message", "Garage activated for mechanic: " + mechanicId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // FIX: missing endpoint required by user-service feign client
    @DeleteMapping("/admin/mechanic/{mechanicId}")
    public ResponseEntity<?> deleteGarageByMechanic(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @PathVariable Long mechanicId) {
        if (!isAdmin(role)) return forbidden();
        try {
            garageService.deleteGarageByMechanicId(mechanicId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyGarage(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long mechanicId) {
        if (!isSupplier(role)) return forbidden();
        try {
            return ResponseEntity.ok(garageService.getMyGarage(mechanicId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long mechanicId,
            @RequestBody UpdateGarageProfileRequest request) {
        if (!isSupplier(role)) return forbidden();
        try {
            return ResponseEntity.ok(garageService.updateProfile(mechanicId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/hours")
    public ResponseEntity<?> updateHours(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long mechanicId,
            @Valid @RequestBody UpdateHoursRequest request) {
        if (!isSupplier(role)) return forbidden();
        try {
            return ResponseEntity.ok(garageService.updateHours(mechanicId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/services")
    public ResponseEntity<?> addService(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long mechanicId,
            @Valid @RequestBody CreateServiceRequest request) {
        if (!isSupplier(role)) return forbidden();
        try {
            ServiceOfferingResponse response = garageService.addService(mechanicId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<?> updateService(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long mechanicId,
            @PathVariable Long id,
            @RequestBody CreateServiceRequest request) {
        if (!isSupplier(role)) return forbidden();
        try {
            return ResponseEntity.ok(garageService.updateService(mechanicId, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteService(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long mechanicId,
            @PathVariable Long id) {
        if (!isSupplier(role)) return forbidden();
        try {
            garageService.deleteService(mechanicId, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    private boolean isAdmin(String role) {
        return "ROLE_ADMIN".equals(role);
    }

    private boolean isSupplier(String role) {
        return "ROLE_SUPPLIER".equals(role);
    }

    private ResponseEntity<Map<String, String>> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
    }
}