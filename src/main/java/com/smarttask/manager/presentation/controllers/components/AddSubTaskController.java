package com.smarttask.manager.presentation.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AddSubTaskController implements Initializable {
    @FXML
    private VBox addSubTaskContainer;
    @FXML private VBox subTaskEditorContainer;

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
        subTaskEditorContainer.setVisible(show);
        subTaskEditorContainer.setManaged(show);

        addSubTaskContainer.setVisible(!show);
        addSubTaskContainer.setManaged(!show);

    }
}
