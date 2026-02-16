package com.smarttask.manager.presentation.controllers.pages;

import com.smarttask.manager.application.usecase.task.TaskUseCase;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.domain.model.TaskStatus;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;
import com.smarttask.manager.infrastructure.session.UserSession;
import com.smarttask.manager.presentation.controllers.components.TaskListController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CalenderController implements Initializable {

    @FXML private VBox mainListContainer;
    @FXML private Label pageTitle;

    private TaskUseCase taskUseCase;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            var connection = DatabaseConnection.getConnection();
            var repository = new PostgresTaskRepository(connection);
            this.taskUseCase = new TaskUseCase(repository);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadCalendarData();
    }

    private void loadCalendarData() {
        if (UserSession.getInstance().getUser() == null) return;
        String userId = UserSession.getInstance().getUser().id();

        mainListContainer.getChildren().clear();

        List<Task> allTasks = taskUseCase.getTasksByOwner(userId);

        List<Task> activeTasks = allTasks.stream()
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .filter(t -> t.getStatus() != TaskStatus.ARCHIVED)
                .filter(t -> t.getDueDate() != null)
                .collect(Collectors.toList());

        LocalDate startDate = LocalDate.now();

        if (!activeTasks.isEmpty()) {
            startDate = activeTasks.stream()
                    .map(t -> t.getDueDate().toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());
        }

        LocalDate endDate = YearMonth.from(startDate).plusMonths(1).atEndOfMonth();

        DateTimeFormatter titleFmt = DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH);
        String startMonth = startDate.format(titleFmt);
        String endMonth = endDate.format(titleFmt);
        String year = startDate.format(DateTimeFormatter.ofPattern("yyyy"));

        String title = startMonth.equals(endMonth)
                ? startMonth + " " + year
                : startMonth + " - " + endMonth + " " + year;

        pageTitle.setText(title.substring(0, 1).toUpperCase() + title.substring(1));

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDate loopDate = date;

            List<Task> tasksForDay = activeTasks.stream()
                    .filter(t -> t.getDueDate().toLocalDate().isEqual(loopDate))
                    .collect(Collectors.toList());

            createDaySection(date, tasksForDay);
        }
    }

    private void createDaySection(LocalDate date, List<Task> tasks) {
        try {
            VBox dayContainer = new VBox();
            dayContainer.getStyleClass().add("todayTask-container");

            HBox headerBox = new HBox(15);
            headerBox.getStyleClass().add("todayTask-banner");
            headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.FRENCH);
            String dateString = date.format(fmt);

            dateString = dateString.substring(0, 1).toUpperCase() + dateString.substring(1);

            Label dateLabel = new Label(dateString);
            dateLabel.getStyleClass().add("todayTask-title");

            if (date.equals(LocalDate.now())) {
                dateLabel.setText(dateString + " (Aujourd'hui)");
                dateLabel.setStyle("-fx-text-fill: #0D89FF;");
            }

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            headerBox.getChildren().addAll(dateLabel, spacer);


            Region divider = new Region();
            divider.setPrefHeight(1);
            divider.setMinHeight(1);
            divider.setMaxHeight(1);
            divider.getStyleClass().add("divider");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/TaskList.fxml"));
            Parent taskListNode = loader.load();

            TaskListController controller = loader.getController();

            controller.displayTasks(tasks);

            controller.setRefreshHandler(this::loadCalendarData);

            VBox listWrapper = new VBox();
            listWrapper.getStyleClass().add("todayTask-layout");
            listWrapper.getChildren().add(taskListNode);

            dayContainer.getChildren().addAll(headerBox, divider, listWrapper);

            mainListContainer.getChildren().add(dayContainer);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossible de charger TaskList.fxml pour la date : " + date);
        }
    }
}