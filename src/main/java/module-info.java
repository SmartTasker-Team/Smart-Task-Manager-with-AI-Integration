/**
 * Defines the module configuration for the Smart Task Manager application.
 * <p>
 * This configuration follows the Clean Architecture approach by strictly defining module dependencies.
 * It requires JavaFX modules for the UI, Java SQL for the local PostgreSQL database, and
 * Jackson/Gson for JSON processing required by the Cloud AI REST APIs.
 * </p>
 *
 * <p><b>Dependencies:</b></p>
 * <ul>
 * <li>{@code javafx.controls}, {@code javafx.fxml}: For the Modern hardware-accelerated UI.</li>
 * <li>{@code java.sql}: For connecting to the local PostgreSQL server.</li>
 * <li>{@code java.net.http}: For communicating with REST APIs and Cloud AI services.</li>
 * </ul>
 *
 * @author Smart Task Manager Team
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
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client;
    requires com.google.api.services.oauth2;
    requires com.google.api.services.calendar;

    // Controllers opened for FXML
    opens com.smarttask.manager.presentation.controllers to javafx.fxml;
    opens com.smarttask.manager.presentation.controllers.onboarding to javafx.fxml;

    // Views (if you use fx:root or injections)
    opens com.smarttask.manager.presentation.views to javafx.fxml;

    // Exported packages
    exports com.smarttask.manager;
    exports com.smarttask.manager.presentation;
    exports com.smarttask.manager.presentation.controllers;
    exports com.smarttask.manager.presentation.views;
    exports com.smarttask.manager.application.dto;
}
