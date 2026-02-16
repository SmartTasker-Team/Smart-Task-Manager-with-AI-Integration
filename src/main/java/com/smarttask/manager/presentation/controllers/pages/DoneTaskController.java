package com.smarttask.manager.presentation.controllers.pages;

import com.smarttask.manager.application.usecase.task.TaskUseCase;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.domain.model.TaskStatus;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;
import com.smarttask.manager.infrastructure.session.UserSession;
import com.smarttask.manager.presentation.controllers.components.TaskListController;
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
        try {
            var connection = DatabaseConnection.getConnection();
            var repository = new PostgresTaskRepository(connection);
            this.taskUseCase = new TaskUseCase(repository);
        } catch (Exception e) {
            System.err.println("❌ Error initializing DoneTaskController: " + e.getMessage());
        }

        if (doneTaskListController != null) {
            doneTaskListController.setRefreshHandler(this::refreshPage);
        }

        refreshPage();
    }

    private void refreshPage() {
        if (taskUseCase == null) return;

        if (UserSession.getInstance().getUser() == null) return;
        String userId = UserSession.getInstance().getUser().id();

        List<Task> allTasks = taskUseCase.getTasksByOwner(userId);

        List<Task> completedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .sorted(Comparator.comparing((Task t) -> t.getCompletedAt() != null ? t.getCompletedAt() : t.getCreatedAt()).reversed())
                .collect(Collectors.toList());

        if (doneTaskListController != null) {
            doneTaskListController.displayTasks(completedTasks);
        }
    }
}