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


public class LoginUseCase {

}
