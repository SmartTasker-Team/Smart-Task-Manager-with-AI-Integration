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

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;

    opens com.smarttask.manager.presentation.controllers to javafx.fxml;
    exports com.smarttask.manager.application.dto;
}