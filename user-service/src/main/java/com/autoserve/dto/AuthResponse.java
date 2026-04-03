package com.autoserve.dto;

public record AuthResponse(String token, Long userId, String role) {
}