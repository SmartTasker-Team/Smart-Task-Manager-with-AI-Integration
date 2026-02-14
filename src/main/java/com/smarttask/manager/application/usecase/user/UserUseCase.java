package com.smarttask.manager.application.usecase.user;

import com.smarttask.manager.application.dto.UserDTO;
import com.smarttask.manager.domain.repository.UserRepository;
import com.smarttask.manager.domain.exception.DomainException;
import com.smarttask.manager.domain.model.User;
import java.util.UUID;

public class UserUseCase {
    private final UserRepository repository;

    public UserUseCase(UserRepository repository) { this.repository = repository; }

    public String register(UserDTO dto) {
        // Check if email exists
        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new DomainException("Email already in use.");
        }


        // In a real app, use: String hash = PasswordService.hash(dto.password());
        String fakeHash = "HASHED_" + "manual_auth_code"; // change it whene we creat manual regesteration form Ui so the user password will be hashed and not the same for all users

        User user = new User(UUID.randomUUID().toString(), dto.username(), dto.email(), fakeHash);
        repository.save(user);
        return user.getIdUser();
    }

    private void validatePasswordStrength(String pass) {
        if (pass == null || pass.length() < 8 || !pass.matches(".*[0-9].*") || !pass.matches(".*[A-Z].*")) {
            throw new DomainException("Password too weak: Needs 8+ chars, 1 digit, 1 uppercase.");
        }
    }

    public void deleteAccount(String userId) {
        repository.delete(userId);
    }
}