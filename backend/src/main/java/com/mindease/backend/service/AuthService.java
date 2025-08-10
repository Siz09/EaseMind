package com.mindease.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.mindease.backend.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
public class AuthService {

    private final FirebaseAuth firebaseAuth;
    private final UserService userService;

    public AuthService(FirebaseAuth firebaseAuth, UserService userService) {
        this.firebaseAuth = firebaseAuth;
        this.userService = userService;
    }

    /**
     * Creates a Firebase user and synchronizes with local database
     */
    public String createUserWithFirebase(String email, String password) {
        try {
            // 1. Create Firebase user
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setEmailVerified(false);  // Requires email verification

            UserRecord userRecord = firebaseAuth.createUser(request);

            // 2. Generate email verification link
            String verificationLink = firebaseAuth.generateEmailVerificationLink(email);
            log.info("Email verification link generated for {}: {}", email, verificationLink);

            // 3. Create local user record (simplified example)
            userService.createLocalUser(userRecord.getUid(), email);

            return userRecord.getUid();

        } catch (FirebaseAuthException e) {
            log.error("Firebase user creation failed for {}: {}", email, e.getMessage());
            throw new AuthException("User registration failed: " + translateFirebaseError(e));
        }
    }

    /**
     * Deletes a Firebase user and cleans up local records
     */
    @Transactional
    public void deleteUser(String uid) {
        try {
            // 1. Delete from Firebase
            firebaseAuth.deleteUser(uid);

            // 2. Delete local user
            userService.deleteUserByFirebaseUid(uid);

        } catch (FirebaseAuthException e) {
            log.error("Failed to delete Firebase user {}: {}", uid, e.getMessage());
            throw new AuthException("User deletion failed");
        }
    }

    /**
     * Generates a custom token for client-side Firebase Auth
     */
    public String generateCustomToken(String uid) {
        try {
            return firebaseAuth.createCustomToken(uid);
        } catch (FirebaseAuthException e) {
            throw new AuthException("Token generation failed");
        }
    }

    private String translateFirebaseError(FirebaseAuthException e) {
        String errorCode = e.getErrorCode().toString();
        switch (errorCode) {
            case "EMAIL_EXISTS":
                return "Email already in use";
            case "INVALID_EMAIL":
                return "Invalid email format";
            case "WEAK_PASSWORD":
                return "Password must be at least 6 characters";
            default:
                return "Authentication error";
        }
    }

}