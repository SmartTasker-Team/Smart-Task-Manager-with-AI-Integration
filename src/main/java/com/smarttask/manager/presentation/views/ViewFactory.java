package com.smarttask.manager.presentation.views;

/**
 * Factory class for creating and initializing JavaFX Views.
 * <p>
 * Responsible for loading FXML resources and injecting dependencies into Controllers,
 * ensuring a clean separation of UI construction logic.
 * </p>
 */

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ViewFactory {

    private final StringProperty clientSelectedMenuItem;

    public ViewFactory() {
        this.clientSelectedMenuItem = new SimpleStringProperty("");
    }

    public StringProperty getClientSelectedMenuItem() {
        return clientSelectedMenuItem;
    }

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

    public Parent loadWelcomeRoot() {
        return load("/fxml/welcome_view.fxml", "/styles/Welcome.css");
    }
    public Parent loadOnboardingStep1() {
        return load("/fxml/onboarding/onboarding_step1.fxml", "/styles/Onboarding1.css");
    }
    public Parent loadOnboardingStep3() {
        return load("/fxml/onboarding/onboarding_step3.fxml", "/styles/Onboarding3.css");
    }
    public Parent loadOnboardingStep4() {
        return load("/fxml/onboarding/onboarding_step4.fxml", "/styles/Onboarding4.css");
    }
    public Parent dashboardView() {
        return load("/fxml/DashboardView.fxml", "/styles/Dashboard.css");
    }
    public Parent getInboxView() {
        return load("/fxml/pages/Inbox.fxml", null);
    }
    public Parent getNotificationView() {
        return load("/fxml/pages/Notifications.fxml", null);
    }
    public Parent getTodayTasksView() {
        return load("/fxml/pages/TodayTasks.fxml", null);
    }
    public Parent getAnalyticsView() {
        return load("/fxml/pages/Analytics.fxml", null);
    }
    public Parent getDoneTasksView() {
        return load("/fxml/pages/DoneTasks.fxml", null);
    }
    public Parent getCalenderView() {
        return load("/fxml/pages/CalenderView.fxml", null);
    }

    private Parent load(String fxml, String css) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            if (css != null) {
                root.getStylesheets().add(getClass().getResource(css).toExternalForm());
            }
            return root;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    }
}
