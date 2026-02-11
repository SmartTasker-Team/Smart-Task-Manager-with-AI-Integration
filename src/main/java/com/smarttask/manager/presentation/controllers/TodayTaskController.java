package com.smarttask.manager.presentation.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.net.URL;
import java.util.ResourceBundle;

public class TodayTaskController implements Initializable {
    @FXML
    private VBox sectionContent;
    @FXML private SVGPath toggleIcon;


    private static final String ICON_EXPANDED  = "m19.5 8.25-7.5 7.5-7.5-7.5";
    private static final String ICON_COLLAPSED = "M4.5 15.75l7.5-7.5 7.5 7.5";

    private boolean isExpanded = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateState();
    }

    @FXML
    private void onToggleSection() {
        isExpanded = !isExpanded;

        updateState();
    }

    private void updateState() {
        sectionContent.setVisible(isExpanded);
        sectionContent.setManaged(isExpanded);

        if (isExpanded) {
            toggleIcon.setContent(ICON_EXPANDED);
        } else {
            toggleIcon.setContent(ICON_COLLAPSED);
        }
    }
}

