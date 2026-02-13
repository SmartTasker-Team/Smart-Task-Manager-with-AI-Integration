package com.smarttask.manager.domain.model;

import com.smarttask.manager.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

public class User {
    private final String idUser; // Identity is immutable
    private final LocalDateTime createdAt;

    private String username;
    private String email;
    private String passwordHash;
    private long versionNumber;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Constructor for creating a NEW User.
     */
    public User(String idUser, String username, String email, String passwordHash) {
        this.idUser = Objects.requireNonNull(idUser);
        this.createdAt = LocalDateTime.now();
        this.versionNumber = 1;

        // Business Rule Validations
        validateUsername(username);
        validateEmail(email);

        this.username = username;
        this.email = email;
        this.passwordHash = Objects.requireNonNull(passwordHash);
    }

    // --- BUSINESS LOGIC ---

    public void updateProfile(String newUsername, String newEmail) {
        validateUsername(newUsername);
        validateEmail(newEmail);

        // Only increment version if data actually changes
        if (!this.username.equals(newUsername) || !this.email.equals(newEmail)) {
            this.username = newUsername;
            this.email = newEmail;
            this.versionNumber++;
        }
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new DomainException("Password hash cannot be empty.");
        }
        this.passwordHash = newPasswordHash;
        this.versionNumber++;
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new DomainException("Invalid email format.");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.length() < 3) {
            throw new DomainException("Username must be at least 3 characters.");
        }
    }

    // --- INFRASTRUCTURE HOOKS ---
    public void loadVersion(long version) { this.versionNumber = version; }

    // --- GETTERS ---
    public String getIdUser() { return idUser; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getVersionNumber() { return versionNumber; }
}