package com.smarttask.manager.presentation.controllers.components.modalDialog;

import com.smarttask.manager.domain.model.PriorityLevel;
import com.smarttask.manager.domain.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class TaskDetailsController {

    @FXML private Button btnCancel;

    @FXML private Label lblTitle;
    @FXML private Label lblDescription;
    @FXML private Label lblDate;
    @FXML private Label lblPriority;
    @FXML private Label lblStatus;

    public void setTask(Task task) {
        if (lblTitle != null) {
            lblTitle.setText(task.getTitle());
        }

        if (lblDescription != null) {
            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                lblDescription.setText(task.getDescription());
            } else {
                lblDescription.setText("No Description provided.");
            }
        }

        if (lblDate != null) {
            if (task.getDueDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm");
                lblDate.setText(task.getDueDate().format(formatter));
            } else {
                lblDate.setText("No Date");
            }
        }

        if (lblPriority != null) {
            String cleanPriority = getPriorityLabel(task.getPriority());
            lblPriority.setText(cleanPriority);
        }

        if (lblStatus != null) {
            lblStatus.setText(capitalize(task.getStatus().name()));
        }
    }

    private String getPriorityLabel(PriorityLevel priority) {
        if (priority == null) return "Low";

        return switch (priority) {
            case URGENT_IMPORTANT -> "Urgent";
            case NOT_URGENT_IMPORTANT -> "High";
            case URGENT_NOT_IMPORTANT -> "Medium";
            case NOT_URGENT_NOT_IMPORTANT -> "Low";
        };
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        if (btnCancel != null && btnCancel.getScene() != null) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }
}