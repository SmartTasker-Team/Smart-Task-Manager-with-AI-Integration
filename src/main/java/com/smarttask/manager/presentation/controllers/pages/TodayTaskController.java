package com.smarttask.manager.presentation.controllers.pages;

import com.smarttask.manager.application.usecase.task.TaskUseCase;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.domain.model.TaskStatus;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;
import com.smarttask.manager.presentation.controllers.components.AddTaskController;
import com.smarttask.manager.presentation.controllers.components.TaskListController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.control.Label;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TodayTaskController implements Initializable {
    @FXML private VBox sectionContent;
    @FXML private SVGPath toggleIcon;

    @FXML private TaskListController lateTaskListController;
    @FXML private TaskListController todayTaskListController;
    @FXML private AddTaskController addTaskSectionController;

    private static final String ICON_EXPANDED  = "m19.5 8.25-7.5 7.5-7.5-7.5";
    private static final String ICON_COLLAPSED = "M4.5 15.75l7.5-7.5 7.5 7.5";
    private boolean isExpanded = true;

    private TaskUseCase taskUseCase;
    @FXML private Label dateHeaderLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateState();
        updateDateHeader();

        if (lateTaskListController != null) lateTaskListController.setRefreshHandler(this::refreshPage);
        if (todayTaskListController != null) todayTaskListController.setRefreshHandler(this::refreshPage);
        if (addTaskSectionController != null) {
            addTaskSectionController.setOnTaskAdded(() -> {
                System.out.println("🔄 New Task Added -> Refreshing Today View...");
                refreshPage();
            });
        }

        refreshPage();
    }
    private void updateDateHeader() {
        if (dateHeaderLabel == null) return;

        LocalDate today = LocalDate.now();


        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.FRENCH);
        String month = capitalize(today.format(monthFormatter)).replace(".", "");

        int day = today.getDayOfMonth();

        DateTimeFormatter dayNameFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH);
        String dayName = capitalize(today.format(dayNameFormatter));

        String finalText = String.format("%s %d ‧ Aujourd'hui ‧ %s", month, day, dayName);

        dateHeaderLabel.setText(finalText);
    }
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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

                LocalDate todayDate = LocalDate.now();

                List<Task> lateTasks = allTasks.stream()
                        .filter(t -> t.getStatus() != TaskStatus.DONE && t.getStatus() != TaskStatus.ARCHIVED)
                        .filter(t -> t.getDueDate() != null)
                        .filter(t -> t.getDueDate().toLocalDate().isBefore(todayDate))
                        .sorted(Comparator.comparing(Task::getDueDate))
                        .collect(Collectors.toList());

                List<Task> todayTasks = allTasks.stream()
                        .filter(t -> t.getStatus() != TaskStatus.DONE && t.getStatus() != TaskStatus.ARCHIVED)
                        .filter(t -> t.getDueDate() != null && t.getDueDate().toLocalDate().isEqual(todayDate))
                        .sorted(Comparator.comparing(Task::getPriority))
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    if (lateTaskListController != null) {
                        lateTaskListController.displayTasks(lateTasks);
                        if (lateTasks.isEmpty() && isExpanded) {
                            isExpanded = false;
                            updateState();
                        }
                    }
                    if (todayTaskListController != null) {
                        todayTaskListController.displayTasks(todayTasks);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("❌ Error loading Today Tasks: " + e.getMessage());
            }
        }).start();
    }


    @FXML
    private void onToggleSection() {
        isExpanded = !isExpanded;
        updateState();
    }

    private void updateState() {
        if (sectionContent != null) {
            sectionContent.setVisible(isExpanded);
            sectionContent.setManaged(isExpanded);
        }

        if (toggleIcon != null) {
            if (isExpanded) {
                toggleIcon.setContent(ICON_EXPANDED);
            } else {
                toggleIcon.setContent(ICON_COLLAPSED);
            }
        }
    }
}