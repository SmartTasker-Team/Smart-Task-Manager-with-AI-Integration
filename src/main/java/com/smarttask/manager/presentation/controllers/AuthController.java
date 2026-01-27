package com.smarttask.manager.presentation.controllers;
/**
 * <h2>AuthController</h2>
 *
 * <p>
 * JavaFX controller handling authentication-related UI actions.
 * </p>
 *
 * <h3>Layer</h3>
 * <p>Presentation Layer</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Handle login and registration events</li>
 *   <li>Delegate logic to use cases</li>
 * </ul>
 */
import javafx.fxml.FXML;
public class AuthController {
    @FXML
    private void handleLogin() {
        System.out.println("Login button clicked!");
    }
}
