package com.smarttask.manager.presentation.controllers.onboarding;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import com.smarttask.manager.presentation.navigation.SceneManager;
import com.smarttask.manager.presentation.views.ViewFactory;

public class OnboardingStep4Controller {

    private final ViewFactory viewFactory = new ViewFactory();

    @FXML private HBox googleCard;
    @FXML private Button passerBtn;
    @FXML private Button connecterBtn;

    private boolean isSelected = false;

    @FXML
    private void handleToggleSelect() {
        isSelected = !isSelected;

        if (isSelected) {
            googleCard.getStyleClass().add("option-card-selected");
            showConnectButton(true);
        } else {
            googleCard.getStyleClass().remove("option-card-selected");
            showConnectButton(false);
        }
    }

    private void showConnectButton(boolean showConnect) {
        connecterBtn.setVisible(showConnect);
        connecterBtn.setManaged(showConnect);

        passerBtn.setVisible(!showConnect);
        passerBtn.setManaged(!showConnect);
    }

    @FXML
    private void onFinish() {
        Parent dashboard = viewFactory.dashboardView();
        SceneManager.getInstance().setRootWithFade(dashboard);
    }
}