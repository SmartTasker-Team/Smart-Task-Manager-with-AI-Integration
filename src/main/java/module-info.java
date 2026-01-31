module com.smarttask.manager {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires google.genai;
    requires com.google.gson;

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
