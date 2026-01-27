module com.smarttask.manager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;

    opens com.smarttask.manager.presentation.controllers to javafx.fxml;
    exports com.smarttask.manager.application.dto;
}