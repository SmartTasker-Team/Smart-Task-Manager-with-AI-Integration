package com.smarttask.manager.presentation.controllers.pages;

import com.smarttask.manager.presentation.controllers.components.AddTaskController;
import com.smarttask.manager.presentation.controllers.components.TaskListController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class InboxController implements Initializable {

    @FXML private AddTaskController addTaskSectionController;
    @FXML private TaskListController taskListSectionController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (addTaskSectionController != null && taskListSectionController != null) {

            addTaskSectionController.setOnTaskAdded(() -> {
                System.out.println("🔄 Dashboard: Refreshing Task List...");
                taskListSectionController.loadTasks();
            });

        }
    }
}
