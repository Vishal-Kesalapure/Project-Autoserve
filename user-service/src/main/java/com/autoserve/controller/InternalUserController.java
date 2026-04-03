package com.autoserve.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoserve.dto.MechanicVerificationResponse;
import com.autoserve.entity.Mechanic;
import com.autoserve.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/mechanics/{id}/verification")
    public ResponseEntity<?> getMechanicVerification(
            @RequestHeader(value = "X-Internal-Call", defaultValue = "false") String internalCall,
            @PathVariable Long id) {
        if (!"true".equalsIgnoreCase(internalCall)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Internal access only"));
        }

        try {
            Mechanic mechanic = userService.findMechanicById(id);
            return ResponseEntity.ok(new MechanicVerificationResponse(
                    mechanic.getId(),
                    mechanic.isVerified(),
                    mechanic.isAccountEnabled()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
