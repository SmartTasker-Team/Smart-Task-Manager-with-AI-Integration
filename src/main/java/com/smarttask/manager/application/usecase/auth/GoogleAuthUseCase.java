package com.smarttask.manager.application.usecase.auth;

import com.smarttask.manager.domain.model.User;
import com.smarttask.manager.domain.repository.UserRepository;
import com.smarttask.manager.domain.exception.DomainException;
import java.util.UUID;

/**
 * Use Case for Google OAuth Authentication.
 *
 * This class handles the business logic for registering or retrieving users
 * who authenticate via Google OAuth. It separates the Google auth flow from
 * traditional email/password authentication.
 *
 * Key Responsibilities:
 * - Register new Google users
 * - Retrieve existing Google users
 * - Validate duplicate email detection
 * - Use the createGoogleUser() factory method for clarity
 */
public class GoogleAuthUseCase {
    private final UserRepository userRepository;

    public GoogleAuthUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String registerOrLoginGoogleUser(String googleId, String username, String email) {
        // Check if email already exists
        var existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.isGoogleUser()) {
                return user.getIdUser();
            }

            throw new DomainException(
                "Email already associated with a traditional account. " +
                "Please log in with your email and password instead."
            );
        }

        User googleUser = User.createGoogleUser(
            UUID.randomUUID().toString(),
            username,
            email
        );

        userRepository.save(googleUser);
        return googleUser.getIdUser();
    }


    public User getGoogleUser(String email) {
        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new DomainException("User not found: " + email));

        if (!user.isGoogleUser()) {
            throw new DomainException("User is not a Google user.");
        }

        return user;
    }
}

