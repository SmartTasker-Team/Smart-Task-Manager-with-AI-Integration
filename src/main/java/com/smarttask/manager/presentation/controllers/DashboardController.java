package com.smarttask.manager.presentation.controllers;

import com.smarttask.manager.models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main application Dashboard.
 * <p>
 * Displays the priority matrix, visual progress tracking, and productivity analytics.
 * Acts as the entry point for most user interactions.
 * </p>
 */
public class DashboardController implements Initializable {

    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observable, oldVal, newVal) -> {
            loadView(newVal);
        });

        String currentView = Model.getInstance().getViewFactory().getClientSelectedMenuItem().get();

        if (currentView == null || currentView.isEmpty()) {
            currentView = "Inbox";
        }

        loadView(currentView);
    }

    private void loadView(String viewName) {
        switch (viewName) {
            case "Inbox":
                contentArea.getChildren().setAll(Model.getInstance().getViewFactory().getInboxView());
                break;
            case "Today":
                contentArea.getChildren().setAll(Model.getInstance().getViewFactory().getTodayTasksView());
                break;
            case "Notification":
                contentArea.getChildren().setAll(Model.getInstance().getViewFactory().getNotificationView());
                break;
            case "Analytics":
                contentArea.getChildren().setAll(Model.getInstance().getViewFactory().getAnalyticsView());
                break;
            case "DoneTasks":
                contentArea.getChildren().setAll(Model.getInstance().getViewFactory().getDoneTasksView());
                break;
            case "Calendar":
                contentArea.getChildren().setAll(Model.getInstance().getViewFactory().getCalenderView());
                break;
            default:
                System.out.println("Unknown view: " + viewName);
                break;
        }
    }
}
