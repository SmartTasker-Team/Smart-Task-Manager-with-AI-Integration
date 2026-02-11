package com.smarttask.manager.presentation.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTaskController implements Initializable{
    @FXML private VBox addTaskContainer;
    @FXML private VBox taskEditorContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showEditor(false);
    }

    @FXML
    private void onShowEditor() {
        showEditor(true);
    }

    @FXML
    private void onCancel() {
        showEditor(false);
    }

    private void showEditor(boolean show) {
        taskEditorContainer.setVisible(show);
        taskEditorContainer.setManaged(show);

        addTaskContainer.setVisible(!show);
        addTaskContainer.setManaged(!show);

    }
}
