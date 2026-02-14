package com.smarttask.manager.application.usecase.auth;
/**
 * Use Case responsible for authenticating a user.
 * <p>
 * This class handles the login lifecycle:
 * <ol>
 * <li>Retrieving the user by username via the Repository.</li>
 * <li>Verifying the provided plain-text password against the stored hash using {@link PasswordHashService}.</li>
 * <li>Generating an {@link com.smarttask.manager.application.dto.AuthResultDTO} containing user details or session tokens upon success.</li>
 * </ol>
 * </p>
 *
 * @throws SecurityException if authentication fails.
 */


import com.google.api.services.oauth2.model.Userinfo;
import com.smarttask.manager.domain.model.User;
import com.smarttask.manager.domain.repository.UserRepository;
import com.smarttask.manager.infrastructure.external.auth.GoogleAuthService;

import java.util.Optional;

public class LoginUseCase {

    private final GoogleAuthService googleAuthService;
    private final GoogleAuthUseCase googleLogic;
    private final UserRepository userRepository;

    public LoginUseCase(GoogleAuthService googleAuthService,
                        GoogleAuthUseCase googleLogic,
                        UserRepository userRepository) {
        this.googleAuthService = googleAuthService;
        this.googleLogic = googleLogic;
        this.userRepository = userRepository;
    }

    public User execute() {
        System.out.println("LoginUseCase: Starting login flow...");

        // 1. Open Browser & Get Google Info
        Userinfo googleUser = googleAuthService.login();

        if (googleUser == null) {
            System.out.println("LoginUseCase: User cancelled the login.");
            return null;
        }

        System.out.println("LoginUseCase: Google Auth success for " + googleUser.getEmail());

        String userId = googleLogic.registerOrLoginGoogleUser(
                googleUser.getId(),
                googleUser.getName(),
                googleUser.getEmail()
        );

        Optional<User> userOptional = userRepository.findByEmail(googleUser.getEmail());

        return userOptional.orElseThrow(() ->
                new RuntimeException("Critical Error: User was registered but cannot be found in DB!")
        );
    }
}
