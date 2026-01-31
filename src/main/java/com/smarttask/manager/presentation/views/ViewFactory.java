package com.smarttask.manager.presentation.views;

/**
 * Factory class for creating and initializing JavaFX Views.
 * <p>
 * Responsible for loading FXML resources and injecting dependencies into Controllers,
 * ensuring a clean separation of UI construction logic.
 * </p>
 */

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {

    /* =========================
       Standalone windows
       ========================= */

    public void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartTask Manager - Login");
            stage.show();

        } catch (IOException e) {
            System.err.println("Could not load LoginView.fxml. Check the file path!");
            e.printStackTrace();
        }
    }

    /* =========================
       Root loaders (scene swap)
       ========================= */

    public Parent loadWelcomeRoot() {
        return load("/fxml/welcome_view.fxml", "/styles/welcome.css");
    }

    public Parent loadOnboardingStep1() {
        return load("/fxml/onboarding/onboarding_step1.fxml", "/styles/Onboarding1.css");
    }

    public Parent loadOnboardingStep2() {
        return load("/fxml/onboarding/onboarding_step2.fxml", "/styles/Onboarding2.css");
    }

    public Parent loadOnboardingStep3() {
        return load("/fxml/onboarding/onboarding_step3.fxml", "/styles/Onboarding3.css");
    }

    public Parent loadOnboardingStep4() {
        return load("/fxml/onboarding/onboarding_step4.fxml", "/styles/Onboarding4.css");
    }

    /* =========================
       Internal helper
       ========================= */

    private Parent load(String fxml, String css) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            if (css != null) {
                root.getStylesheets().add(
                        getClass().getResource(css).toExternalForm()
                );
            }

            return root;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    }
}
