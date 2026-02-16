package com.smarttask.manager.presentation.controllers;

import com.smarttask.manager.application.dto.UserDTO;
import com.smarttask.manager.application.usecase.auth.GoogleAuthUseCase;
import com.smarttask.manager.application.usecase.auth.LoginUseCase;
import com.smarttask.manager.domain.model.User;
import com.smarttask.manager.domain.repository.UserRepository;
import com.smarttask.manager.infrastructure.external.auth.GoogleAuthService;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresUserRepository;
import com.smarttask.manager.infrastructure.session.UserSession;
import com.smarttask.manager.presentation.navigation.SceneManager; // Make sure this import is correct
import com.smarttask.manager.presentation.views.ViewFactory;       // Make sure this import is correct

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    private LoginUseCase loginUseCase;

    private final ViewFactory viewFactory = new ViewFactory();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            UserRepository userRepo = new PostgresUserRepository(conn);
            GoogleAuthService authService = new GoogleAuthService();
            GoogleAuthUseCase googleLogic = new GoogleAuthUseCase(userRepo);

            authService.setGoogleAuthUseCase(googleLogic);

            this.loginUseCase = new LoginUseCase(authService, googleLogic, userRepo);
            System.out.println("✅ Auth System Initialized");

        } catch (Exception e) {
            System.err.println("❌ Critical Error initializing Auth: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onGoogleLogin(ActionEvent event) {
        System.out.println("🖱️ UI Action: Google Login Clicked");

        if (loginUseCase == null) {
            showAlert("Error", "Authentication system is not ready.");
            return;
        }

        new Thread(() -> {
            try {
                User domainUser = loginUseCase.execute();

                if (domainUser != null) {
                    UserDTO userDTO = new UserDTO(
                            domainUser.getIdUser(),
                            domainUser.getEmail(),
                            domainUser.getUsername()
                    );
                    UserSession.getInstance().login(userDTO);
                    System.out.println("✅ User " + userDTO.username() + " logged in!");

                    Platform.runLater(this::onContinue);

                } else {
                    Platform.runLater(() -> showAlert("Login Failed", "Google login was cancelled."));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Error", "Login error: " + e.getMessage()));
            }
        }).start();
    }
    @FXML
    private void onContinue() {
        System.out.println("🚀 Navigating to Onboarding Step 1...");

        try {
            Parent onboardingStep1 = viewFactory.loadOnboardingStep1();
            SceneManager.getInstance().setRootWithFade(onboardingStep1);

        } catch (Exception e) {
            System.err.println("Failed to load Onboarding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}