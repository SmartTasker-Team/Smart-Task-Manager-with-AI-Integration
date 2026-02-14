package com.smarttask.manager.presentation.controllers.components.modalDialog;

import javafx.fxml.FXML;
import javafx.scene.control.Button; // Changed from TextField
import javafx.stage.Stage;

public class TaskDetailsController {

    // 1. Change this to match the button in your FXML
    @FXML
    private Button btnCancel;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        // 2. Use btnCancel to find the window
        if (btnCancel != null && btnCancel.getScene() != null) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }
}