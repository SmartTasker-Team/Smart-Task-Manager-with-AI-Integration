package com.smarttask.manager.infrastructure.session;

import com.smarttask.manager.application.dto.UserDTO;

public class UserSession {

    private static UserSession instance;

    private UserDTO currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(UserDTO user) {
        this.currentUser = user;
    }

    public String getUserId() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in!");
        }
        return currentUser.id();
    }

    public String getUsername() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in!");
        }
        return currentUser.username();
    }

    public UserDTO getUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }
}
