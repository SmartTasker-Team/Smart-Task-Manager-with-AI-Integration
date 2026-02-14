package com.smarttask.manager.presentation.controllers.components.modalDialog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EditTaskPopupController {
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
