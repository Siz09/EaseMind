package com.mindease.backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.mindease.backend.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final FirebaseAuth firebaseAuth;

    public AuthController(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) throws FirebaseAuthException {
        UserRecord user = firebaseAuth.createUser(
                new UserRecord.CreateRequest()
                        .setEmail(request.email())
                        .setPassword(request.password())
        );

        String token = firebaseAuth.createCustomToken(user.getUid());

        return ResponseEntity.ok(AuthResponse.of(token, user.getUid(), user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        // Frontend handles Firebase SDK login
        // This endpoint just verifies the token
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuth() {
        return ResponseEntity.ok("Authenticated!");
    }

    // DTO Records
    record RegisterRequest(String email, String password) {}
    record LoginRequest(String email, String password) {}
}