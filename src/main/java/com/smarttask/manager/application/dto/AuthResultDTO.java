package com.smarttask.manager.application.dto;
/**
 * <h2>AuthResultDTO</h2>
 *
 * <p>
 * Data Transfer Object representing the result of an authentication operation
 * such as login or user registration.
 * </p>
 *
 * <p>
 * This DTO safely communicates authentication outcomes from the
 * Application layer to the Presentation layer.
 * </p>
 *
 * <h3>Layer</h3>
 * <p>Application Layer (DTO)</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Encapsulate authentication success or failure</li>
 *   <li>Standardize authentication responses</li>
 * </ul>
 */


public record AuthResultDTO(
        boolean success,
        String message,
        UserDTO authenticatedUser,
        String token
) {
    //Méthode utilitaire pour créer un résultat de succès.

    public static AuthResultDTO success(UserDTO user, String token) {
        return new AuthResultDTO(true, "Authentification réussie", user, token);
    }

    //Méthode utilitaire pour créer un résultat d'échec.

    public static AuthResultDTO failure(String errorMessage) {
        return new AuthResultDTO(false, errorMessage, null, null);
    }
}
