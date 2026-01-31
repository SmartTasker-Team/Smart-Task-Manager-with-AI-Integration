package com.smarttask.manager.presentation.controllers.onboarding;

import com.smarttask.manager.presentation.navigation.SceneManager;
import com.smarttask.manager.presentation.views.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

public class OnboardingStep3Controller {

    private final SceneManager sceneManager = SceneManager.getInstance();
    private final ViewFactory viewFactory = new ViewFactory();

    @FXML private HBox cardA, cardB, cardC, cardD;

    private String selectedOption = "";

    @FXML
    public void initialize() {
        // No longer using RadioButtons or ToggleGroups here
    }

    @FXML
    private void handleOptionA() { selectOption(cardA, "Paper/Note"); }
    @FXML
    private void handleOptionB() { selectOption(cardB, "Other Apps"); }
    @FXML
    private void handleOptionC() { selectOption(cardC, "Calendar"); }
    @FXML
    private void handleOptionD() { selectOption(cardD, "Memory"); }

    private void selectOption(HBox selectedHB, String value) {
        // Reset all cards to default style
        cardA.getStyleClass().remove("option-card-selected");
        cardB.getStyleClass().remove("option-card-selected");
        cardC.getStyleClass().remove("option-card-selected");
        cardD.getStyleClass().remove("option-card-selected");

        // Highlight the selected one
        selectedHB.getStyleClass().add("option-card-selected");
        this.selectedOption = value;
    }

    @FXML
    private void onNext() {
        if (!selectedOption.isEmpty()) {
            System.out.println("Selected method: " + selectedOption);
        }

        Parent next = viewFactory.loadOnboardingStep4();
        sceneManager.setRootWithFade(next);
    }
}