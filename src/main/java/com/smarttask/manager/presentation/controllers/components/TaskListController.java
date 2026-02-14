package com.smarttask.manager.presentation.controllers.components;

import com.smarttask.manager.application.usecase.task.TaskUseCase;
import com.smarttask.manager.domain.model.PriorityLevel;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TaskListController implements Initializable{

    @FXML private VBox openTaskDetails;

    @FXML private VBox taskListContainer; // Make sure your root VBox in FXML has fx:id="taskListContainer"

    private TaskUseCase taskUseCase;
    private final String CURRENT_USER = "user123";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            var connection = DatabaseConnection.getConnection();
            var repository = new PostgresTaskRepository(connection);
            this.taskUseCase = new TaskUseCase(repository);

            loadTasks();
        } catch (Exception e) {
            System.err.println("Error initializing TaskList: " + e.getMessage());
        }
    }

    private void openTaskDetails(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/TaskDetails.fxml"));
            Parent root = loader.load();

            // TaskDetailsController controller = loader.getController();
            // controller.setTask(task);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            // 👇 CORRECTION ICI : Utilisez taskListContainer au lieu de openTaskDetails
            Stage ownerStage = (Stage) taskListContainer.getScene().getWindow();

            stage.initOwner(ownerStage);
            stage.initModality(Modality.WINDOW_MODAL);

            // Centrer ou couvrir la fenêtre parente (selon votre design actuel)
            stage.setX(ownerStage.getX());
            stage.setY(ownerStage.getY());
            stage.setWidth(ownerStage.getWidth());
            stage.setHeight(ownerStage.getHeight());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

            // Rafraîchir la liste après la fermeture des détails
            loadTasks();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private Button openEditTask;

    private void openEditTask(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/EditTaskPopup.fxml"));
            Parent root = loader.load();

            // EditTaskPopupController controller = loader.getController();
            // controller.setTask(task);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            // 👇 C'EST ICI LA CORRECTION IMPORTANTE 👇
            // On utilise taskListContainer au lieu du bouton qui n'existe plus
            Stage ownerStage = (Stage) taskListContainer.getScene().getWindow();

            stage.initOwner(ownerStage);
            stage.initModality(Modality.WINDOW_MODAL);

            // Centrer ou couvrir la fenêtre parente (selon votre design actuel)
            stage.setX(ownerStage.getX());
            stage.setY(ownerStage.getY());
            stage.setWidth(ownerStage.getWidth());
            stage.setHeight(ownerStage.getHeight());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

            loadTasks();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTasks() {
        if (taskUseCase == null || taskListContainer == null) return;

        List<Task> tasks = taskUseCase.getTasksByOwner(CURRENT_USER);
        taskListContainer.getChildren().clear();

        for (Task task : tasks) {
            VBox taskRow = createTaskRow(task);

            // Add the divider line after each task
            Region divider = new Region();
            divider.setPrefHeight(1);
            divider.setMinHeight(1);
            divider.setMaxHeight(1);
            divider.getStyleClass().add("divider");

            taskListContainer.getChildren().addAll(taskRow, divider);
        }
    }

    private VBox createTaskRow(Task task) {
        // 1. Root VBox for the row
        VBox rowBox = new VBox();
        rowBox.getStyleClass().add("task-row");
        rowBox.setOnMouseClicked(e -> openTaskDetails(task));

        // 2. HBox Container
        HBox hbox = new HBox(12);
        hbox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        // 3. Radio Button (Color based on priority)
        RadioButton radioButton = new RadioButton();
        String priorityColorClass = getPriorityColorClass(task.getPriority());
        radioButton.getStyleClass().add("task-radio-" + priorityColorClass);

        // 4. Texts Container (Title + Date/SVGs)
        VBox textVBox = new VBox(3);

        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("task-title");

        HBox metaHBox = new HBox(2);
        metaHBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // 1. Better Date Format (Example: "Ven. 14 Fév, 07:00")
        String dateText = "No Date";
        if (task.getDueDate() != null) {
            // Updated Pattern: Day Name, Day Number, Month, Time
            dateText = task.getDueDate().format(DateTimeFormatter.ofPattern("EEE d MMM, HH:mm"));
        }

        Label dateLabel = new Label(dateText);
        dateLabel.getStyleClass().add("task-date-" + priorityColorClass);

        // 2. IMPORTANT: Prevent the text from being cut off (truncated)
        dateLabel.setMinWidth(Region.USE_PREF_SIZE);
        dateLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);

        // Calendar SVG
        SVGPath calIcon = createSVG("M5 4 H19 A2 2 0 0 1 21 6 V20 A2 2 0 0 1 19 22 H5 A2 2 0 0 1 3 20 V6 A2 2 0 0 1 5 4 Z M16 2 V6 M8 2 V6 M3 10 H21",
                getPriorityHex(task.getPriority()));

        // Recurring SVG (Only if recurring)
        SVGPath recurringIcon = null;
        if (task.isRecurring()) {
            recurringIcon = createSVG("M16.023 9.348h4.992v-.001 M2.985 19.644v-4.992 m0 0h4.992 m-4.993 0 3.181 3.183 a8.25 8.25 0 0 0 13.803-3.7 M4.031 9.865 a8.25 8.25 0 0 1 13.803-3.7 l3.181 3.182 m0-4.991v4.99",
                    getPriorityHex(task.getPriority()));
        }

        // Project/Category SVG (Grey)
        SVGPath folderIcon = createSVG("M14.857 17.082 a23.848 23.848 0 0 0 5.454 -1.31 A8.967 8.967 0 0 1 18 9.75 V9 A6 6 0 0 0 6 9 v0.75 a8.967 8.967 0 0 1 -2.312 6.022 c1.733 0.64 3.56 1.085 5.455 1.31 m5.714 0 a24.255 24.255 0 0 1 -5.714 0 m5.714 0 a3 3 0 1 1 -5.714 0 M3.124 7.5 A8.969 8.969 0 0 1 5.292 3 m13.416 0 a8.969 8.969 0 0 1 2.168 4.5",
                "#495057");

        metaHBox.getChildren().addAll(dateLabel, calIcon);
        if (recurringIcon != null) metaHBox.getChildren().add(recurringIcon);
        metaHBox.getChildren().add(folderIcon);

        textVBox.getChildren().addAll(titleLabel, metaHBox);

        // 5. Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 6. Action Buttons
        Button editBtn = createIconButton("M12 20h9 M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z", 0.75);
        editBtn.setOnAction(e -> openEditTask(task));

        Button deleteBtn = createIconButton("m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0", 0.85);
        deleteBtn.setOnAction(e -> deleteTask(task)); // You can implement delete later

        // Assemble HBox
        hbox.getChildren().addAll(radioButton, textVBox, spacer, editBtn, deleteBtn);
        rowBox.getChildren().add(hbox);

        return rowBox;
    }

    // --- Helpers ---

    private String getPriorityColorClass(PriorityLevel p) {
        if (p == PriorityLevel.URGENT_IMPORTANT) return "red";    // Urgent
        if (p == PriorityLevel.NOT_URGENT_IMPORTANT) return "orange"; // High
        return "purple"; // Medium & Low
    }

    private String getPriorityHex(PriorityLevel p) {
        if (p == PriorityLevel.URGENT_IMPORTANT) return "#db4c3f"; // Red
        if (p == PriorityLevel.NOT_URGENT_IMPORTANT) return "#ff9a1f"; // Orange
        return "#a842f5"; // Purple
    }

    private SVGPath createSVG(String content, String colorHex) {
        SVGPath svg = new SVGPath();
        svg.setContent(content);
        svg.setFill(Color.TRANSPARENT);
        svg.setStroke(Color.web(colorHex));
        svg.setStrokeWidth(2);
        svg.setScaleX(0.6);
        svg.setScaleY(0.6);
        return svg;
    }

    private Button createIconButton(String svgContent, double scale) {
        Button btn = new Button();
        btn.getStyleClass().add("icon-button-transparent");
        btn.setPrefSize(30, 30);

        SVGPath svg = new SVGPath();
        svg.setContent(svgContent);
        svg.setFill(Color.TRANSPARENT);
        svg.setStroke(Color.web("#939595"));
        svg.setStrokeWidth(1.75);
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        svg.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        svg.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

        btn.setGraphic(svg);
        return btn;
    }
    private void deleteTask(Task task) {
        System.out.println("Delete task: " + task.getTitle());
    }
}
