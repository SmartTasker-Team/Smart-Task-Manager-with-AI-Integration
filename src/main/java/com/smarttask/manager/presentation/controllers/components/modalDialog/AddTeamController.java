package com.smarttask.manager.presentation.controllers.components.modalDialog;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTeamController {
    @FXML
    private TextField nameField;

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();

        System.out.println("Creating Project: " + name );

        // TODO: Call your backend service here to save the project

        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
