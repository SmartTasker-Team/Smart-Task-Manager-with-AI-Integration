package com.smarttask.manager.presentation.controllers.onboarding;

import com.smarttask.manager.infrastructure.external.auth.GoogleAuthService;
import com.smarttask.manager.presentation.navigation.SceneManager;
import com.smarttask.manager.presentation.views.ViewFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class OnboardingStep4Controller {

    private final ViewFactory viewFactory = new ViewFactory();
    private final GoogleAuthService authService = new GoogleAuthService(); // Initialize Google Service

    @FXML private HBox googleCard;
    @FXML private Button passerBtn;
    @FXML private Button connecterBtn;

    private boolean isSelected = false;

    @FXML
    private void handleToggleSelect() {
        isSelected = !isSelected;

        if (isSelected) {
            if (!googleCard.getStyleClass().contains("option-card-selected")) {
                googleCard.getStyleClass().add("option-card-selected");
            }
            showConnectButton(true);
        } else {
            googleCard.getStyleClass().remove("option-card-selected");
            showConnectButton(false);
        }
    }

    private void showConnectButton(boolean showConnect) {
        connecterBtn.setVisible(showConnect);
        connecterBtn.setManaged(showConnect);

        passerBtn.setVisible(!showConnect);
        passerBtn.setManaged(!showConnect);
    }

    /**
     * Triggered when "Connecter" is clicked.
     * Starts the Google Calendar authorization flow.
     */
    @FXML
    private void onConnect() {
        connecterBtn.setDisable(true);
        connecterBtn.setText("Connexion...");

        // 1. Run auth in background thread to keep UI responsive
        Task<Boolean> authTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // This opens the browser for Google OAuth
                return authService.connectCalendar();
            }
        };

        // 2. Success Handler
        authTask.setOnSucceeded(event -> {
            boolean success = authTask.getValue();
            if (success) {
                System.out.println("✅ Google Calendar Connected Successfully!");
                Platform.runLater(() -> {
                    connecterBtn.setText("Connecté !");
                    // Automatically proceed to Dashboard after short delay
                    goToDashboard();
                });
            } else {
                Platform.runLater(() -> {
                    showAlert("Échec", "La connexion à Google Agenda a échoué.");
                    resetConnectButton();
                });
            }
        });

        // 3. Error Handler
        authTask.setOnFailed(event -> {
            Throwable error = authTask.getException();
            System.err.println("❌ Auth Error: " + error.getMessage());
            error.printStackTrace();
            Platform.runLater(() -> {
                showAlert("Erreur", "Une erreur est survenue: " + error.getMessage());
                resetConnectButton();
            });
        });

        new Thread(authTask).start();
    }

    /**
     * Triggered when "Passer" (Skip) is clicked.
     */
    @FXML
    private void onFinish() {
        System.out.println("⏩ Skipping Calendar connection...");
        goToDashboard();
    }

    private void goToDashboard() {
        try {
            Parent dashboard = viewFactory.dashboardView();
            SceneManager.getInstance().setRootWithFade(dashboard);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le Dashboard.");
        }
    }

    private void resetConnectButton() {
        connecterBtn.setDisable(false);
        connecterBtn.setText("Connecter");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}