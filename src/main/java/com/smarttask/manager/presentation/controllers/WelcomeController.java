package com.smarttask.manager.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import com.smarttask.manager.presentation.navigation.SceneManager;
import com.smarttask.manager.presentation.views.ViewFactory;

public class WelcomeController {

    private final ViewFactory viewFactory = new ViewFactory();

    @FXML
    private void onContinue() {
        System.out.println("Continue clicked"); // debug (optional)

        Parent onboardingStep1 = viewFactory.loadOnboardingStep1();
        SceneManager.getInstance().setRootWithFade(onboardingStep1);
    }
}
