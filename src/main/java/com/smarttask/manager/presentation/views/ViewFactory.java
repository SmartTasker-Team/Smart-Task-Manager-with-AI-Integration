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
}
