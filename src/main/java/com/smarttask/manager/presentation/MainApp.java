package com.smarttask.manager.presentation;

import javafx.application.Application;
import javafx.stage.Stage;
import com.smarttask.manager.presentation.views.ViewFactory;
import com.smarttask.manager.presentation.navigation.SceneManager;
import javafx.scene.Parent;
import javafx.scene.image.Image;

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
        ViewFactory factory = new ViewFactory();

        stage.setScene(manager.getScene());
        stage.setTitle("TaskIQ");
        stage.show();

        TaskIQIntro intro = new TaskIQIntro();
        Parent introRoot = intro.createRoot();

        intro.setOnFinished(() -> {
            Parent welcome = factory.loadWelcomeRoot();
            manager.setRootWithFade(welcome);
        });

        manager.setRoot(introRoot);
    }

    public static void main(String[] args) {
        launch(args);
    }
}