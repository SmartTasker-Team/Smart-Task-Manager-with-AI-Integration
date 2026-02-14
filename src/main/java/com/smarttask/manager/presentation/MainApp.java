package com.smarttask.manager.presentation;

import com.smarttask.manager.models.Model;
import com.smarttask.manager.presentation.navigation.SceneManager;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/Images/logo_icon.png")),
                new Image(getClass().getResourceAsStream("/Images/logo_icon.png")),
                new Image(getClass().getResourceAsStream("/Images/logo_icon.png")),
                new Image(getClass().getResourceAsStream("/Images/logo_icon.png")),
                new Image(getClass().getResourceAsStream("/Images/logo_icon.png"))
        );

        SceneManager manager = SceneManager.getInstance();

        boolean isLoggedIn = Model.getInstance().isUserLoggedIn();
        
        stage.setScene(manager.getScene());

        manager.getScene().getStylesheets().add(
                getClass().getResource("/styles/Dashboard.css").toExternalForm()
        );
        
        if (isLoggedIn) {
            manager.setRoot(Model.getInstance().getViewFactory().dashboardView());
            stage.setScene(manager.getScene());
            stage.setTitle("TaskIQ - Dashboard");
            stage.show();

        } else {
            stage.setScene(manager.getScene());
            stage.setTitle("TaskIQ");
            stage.show();

            TaskIQIntro intro = new TaskIQIntro();
            Parent introRoot = intro.createRoot();

            intro.setOnFinished(() -> {
                Parent welcome = Model.getInstance().getViewFactory().loadWelcomeRoot();
                manager.setRootWithFade(welcome);
            });

            manager.setRoot(introRoot);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}