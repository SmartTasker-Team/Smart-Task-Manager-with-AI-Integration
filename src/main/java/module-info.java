/**
 * Defines the module configuration for the Smart Task Manager application.
 */
module com.smarttask.manager {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.httpserver;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires google.genai;
    requires com.google.gson;
    requires vosk;
    requires java.desktop;
    requires javafx.graphics;

    // Google API Dependencies
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client;
    requires com.google.api.services.oauth2;
    requires com.google.api.services.calendar;

    // REMOVED: requires com.smarttask.manager; (This caused the error)

    // --- EXPORTS & OPENS ---

    // Allow JavaFX to access controllers via reflection
    opens com.smarttask.manager.presentation.controllers to javafx.fxml;
    opens com.smarttask.manager.presentation.controllers.onboarding to javafx.fxml;
    opens com.smarttask.manager.presentation.views to javafx.fxml;

    // Specifically for your new Modal Dialog
    opens com.smarttask.manager.presentation.controllers.components.modalDialog to javafx.fxml;
    exports com.smarttask.manager.presentation.controllers.components.modalDialog;

    // Other exports
    exports com.smarttask.manager;
    exports com.smarttask.manager.presentation;
    exports com.smarttask.manager.presentation.controllers;
    exports com.smarttask.manager.presentation.views;
    exports com.smarttask.manager.application.dto;

    exports com.smarttask.manager.presentation.controllers.pages;
    opens com.smarttask.manager.presentation.controllers.pages to javafx.fxml;

    exports com.smarttask.manager.presentation.controllers.components;
    opens com.smarttask.manager.presentation.controllers.components to javafx.fxml;
}