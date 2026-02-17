package com.smarttask.manager.presentation.navigation;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
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
}
