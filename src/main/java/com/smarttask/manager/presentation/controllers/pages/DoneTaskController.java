package com.smarttask.manager.presentation.controllers.pages;

import com.smarttask.manager.application.usecase.task.TaskUseCase;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.domain.model.TaskStatus;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;
import com.smarttask.manager.presentation.controllers.components.TaskListController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DoneTaskController implements Initializable {

    @FXML private TaskListController doneTaskListController;

    private TaskUseCase taskUseCase;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (doneTaskListController != null) {
            doneTaskListController.setRefreshHandler(this::refreshPage);
        }
        refreshPage();
    }

    private void refreshPage() {
        new Thread(() -> {
            try {
                if (this.taskUseCase == null) {
                    var connection = DatabaseConnection.getConnection();
                    var repository = new PostgresTaskRepository(connection);
                    this.taskUseCase = new TaskUseCase(repository);
                }

                String userId = "685f976d-ef7d-4209-bea2-0ec5ffea7571";
                List<Task> allTasks = taskUseCase.getTasksByOwner(userId);

                List<Task> completedTasks = allTasks.stream()
                        .filter(t -> t.getStatus() == TaskStatus.DONE)
                        .sorted(Comparator.comparing((Task t) -> t.getCompletedAt() != null ? t.getCompletedAt() : t.getCreatedAt()).reversed())
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    if (doneTaskListController != null) {
                        doneTaskListController.displayTasks(completedTasks);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
