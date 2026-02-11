package com.smarttask.manager.presentation.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;

public class AddSectionController implements Initializable {
    @FXML private VBox addSectionContainer;
    @FXML private VBox sectionContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showSectionForm(false);
    }

    @FXML
    private void onShowSectionForm() {
        showSectionForm(true);
    }

    @FXML
    private void onCancel() {
        showSectionForm(false);
    }

    private void showSectionForm(boolean show) {
        sectionContainer.setVisible(show);
        sectionContainer.setManaged(show);

        addSectionContainer.setVisible(!show);
        addSectionContainer.setManaged(!show);

    }
}
