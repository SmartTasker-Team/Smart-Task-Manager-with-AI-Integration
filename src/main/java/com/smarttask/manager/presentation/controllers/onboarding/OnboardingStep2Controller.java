package com.smarttask.manager.presentation.controllers.onboarding;

import com.smarttask.manager.presentation.navigation.SceneManager;
import com.smarttask.manager.presentation.views.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;

public class OnboardingStep2Controller {


    private final SceneManager sceneManager = SceneManager.getInstance();
    private final ViewFactory viewFactory = new ViewFactory();

    @FXML
    private void onNext() {
        Parent next = viewFactory.loadOnboardingStep3();
        sceneManager.setRootWithFade(next);
    }
}
