package com.smarttask.manager.application.usecase.auth;
/**
 * Interface defining the contract for password cryptography services.
 * <p>
 * This interface acts as a Port in the Clean Architecture, decoupling the application layer
 * from specific security implementations (e.g., BCrypt, Argon2).
 * </p>
 * <p>
 * Implementations reside in the Infrastructure layer (e.g., {@code PasswordHashServiceImpl}).
 * </p>
 */


public class PasswordHashService {
}
