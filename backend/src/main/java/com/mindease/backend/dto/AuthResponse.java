package com.mindease.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuthResponse {
    @JsonProperty("access_token")
    private String token;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("refresh_token")
    private String refreshToken;  // For token rotation

    @JsonProperty("issued_at")
    private Instant issuedAt;

    // Static factory method
    public static AuthResponse of(String token, String userId, String email) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(3600)  // 1 hour expiration
                .userId(userId)
                .email(email)
                .issuedAt(Instant.now())
                .build();
    }
}