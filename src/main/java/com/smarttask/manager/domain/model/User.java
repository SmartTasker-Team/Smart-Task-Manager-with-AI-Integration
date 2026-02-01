package com.smarttask.manager.domain.model;

import java.time.Instant;
import java.util.regex.Pattern;


/**
 * Entity representing a team member in the system.
 * <p>
 * Identities users within the local network context to enable task sharing and
 * conflict resolution.
 * </p>
 */

public class User {
    private final String idUser;
    private String username;
    private String email;
    private String passwordHash;
    private long versionNumber;
    private Instant createdAt;

    // Pattern de validation d'email (Règle métier)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public User(String idUser, String username, String email, String passwordHash) {
        validateEmail(email);
        validatePasswordStrength(passwordHash);
        if (username == null || username.length() < 3) {
            throw new IllegalArgumentException("Le nom d'utilisateur doit avoir au moins 3 caractères");
        }

        this.idUser = idUser;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.versionNumber = 1;
        this.createdAt = Instant.now();
    }

    // --- MÉTHODES MÉTIERS ---

    public static void validatePasswordStrength(String password) {
        if (password == null || password.isBlank() || password.length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins un chiffre.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une majuscule.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
    }



    // --- GETTERS ---
    public String getIdUser() { return idUser; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String newEmail) {
        validateEmail(newEmail);
        this.email = newEmail;
        this.versionNumber++;
    }
    public void setPassword(String newPasswordHash) {
        validatePasswordStrength(newPasswordHash);
        this.passwordHash = newPasswordHash;
        this.versionNumber++;
    }

    public String toString(){
        return "User{" +
                "idUser='" + idUser + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", createdAt=" + createdAt +
                '}';
    };

    public static void main(String[] args){
        User user1 = new User("222222222","gdgdgdg","hdhdhdhd@gmail.com","Hgd11gdgdgdgd");
        System.out.println(user1);
        user1.setEmail("houdaifa@gmail.com");
        System.out.println(user1);
    };

}
