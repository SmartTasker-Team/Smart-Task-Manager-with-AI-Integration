package com.smarttask.manager.application.usecase.auth;
/**
 * Use Case encapsulating the business logic for registering a new user.
 * <p>
 * This class orchestrates the sign-up process by:
 * <ol>
 * <li>Validating the {@link com.smarttask.manager.application.dto.RegisterRequestDTO} input.</li>
 * <li>Ensuring the username or email is not already taken via the {@link com.smarttask.manager.domain.repository.UserRepository}.</li>
 * <li>Hashing the password securely using the {@link PasswordHashService}.</li>
 * <li>Creating and persisting the new {@link com.smarttask.manager.domain.model.User} entity.</li>
 * </ol>
 * </p>
 */


public class RegisterUseCase {
}
