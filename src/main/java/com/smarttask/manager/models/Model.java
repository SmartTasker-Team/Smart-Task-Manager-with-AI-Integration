package com.smarttask.manager.models;

import com.smarttask.manager.presentation.views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;

    private boolean isUserLoggedIn = false;

    private Model() {
        this.viewFactory = new ViewFactory();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        isUserLoggedIn = userLoggedIn;
    }
}