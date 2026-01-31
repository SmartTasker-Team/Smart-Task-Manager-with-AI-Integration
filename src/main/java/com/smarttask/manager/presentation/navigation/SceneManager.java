package com.smarttask.manager.presentation.navigation;

import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.util.Duration;
import javafx.scene.Parent;

public class SceneManager {
    private static SceneManager instance;

    private final Scene scene;
    private final StackPane container;

    public SceneManager() {
        container = new StackPane();
        scene = new Scene(container);
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public Scene getScene() {
        return scene;
    }

    public void setRoot(Parent newRoot) {
        container.getChildren().setAll(newRoot);
    }

    public void setRootWithFade(Parent newRoot) {
        Parent oldRoot = container.getChildren().isEmpty()
                ? null
                : (Parent) container.getChildren().get(0);

        if (oldRoot == null) {
            container.getChildren().add(newRoot);
            return;
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldRoot);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            container.getChildren().setAll(newRoot);
            newRoot.setOpacity(0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }
}
