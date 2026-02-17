package com.smarttask.manager.presentation.controllers;
import com.smarttask.manager.presentation.navigation.SceneManager;
import com.smarttask.manager.presentation.views.ViewFactory;

import javafx.fxml.FXML;
import javafx.scene.Parent;

public class AuthController {
    private final SceneManager sceneManager = SceneManager.getInstance();
    private final ViewFactory viewFactory = new ViewFactory();

    @FXML
    private void onContinue() {
        System.out.println("🚀 Navigating to Onboarding Step 1...");

        try {
            Parent onboardingStep1 = viewFactory.loadOnboardingStep1();
            sceneManager.setRoot(onboardingStep1);

        } catch (Exception e) {
            System.err.println("Failed to load Onboarding: " + e.getMessage());
        }
    }
}