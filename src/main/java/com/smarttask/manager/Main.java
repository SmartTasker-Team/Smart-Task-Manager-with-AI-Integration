package com.smarttask.manager;

import javafx.application.Application;
import javafx.stage.Stage;
import com.smarttask.manager.presentation.views.ViewFactory;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.showLoginWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}