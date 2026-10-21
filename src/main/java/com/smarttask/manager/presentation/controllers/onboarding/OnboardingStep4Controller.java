package com.smarttask.manager.presentation.controllers.onboarding;

import com.smarttask.manager.presentation.navigation.SceneManager;
import com.smarttask.manager.presentation.views.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class OnboardingStep4Controller {
    private final SceneManager sceneManager = SceneManager.getInstance();
    private final ViewFactory viewFactory = new ViewFactory();


    @FXML private HBox googleCard;
    @FXML private Button passerBtn;
    @FXML private Button connecterBtn;

    private boolean isSelected = false;

    @FXML
    private void handleToggleSelect() {
        isSelected = !isSelected;

        if (isSelected) {
            if (!googleCard.getStyleClass().contains("option-card-selected")) {
                googleCard.getStyleClass().add("option-card-selected");
            }
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
        System.out.println("🚀 Navigating to Onboarding Step 4...");
        Parent dashboard = viewFactory.dashboardView();
        sceneManager.setRoot(dashboard);
    }

}