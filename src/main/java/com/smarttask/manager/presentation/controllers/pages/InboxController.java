package com.smarttask.manager.presentation.controllers.pages;

import com.smarttask.manager.application.dto.ProductivityReport;
import com.smarttask.manager.application.usecase.analytics.GenerateProductivityReportUseCase;
import com.smarttask.manager.infrastructure.session.UserSession;
import com.smarttask.manager.presentation.controllers.components.AddTaskController;
import com.smarttask.manager.presentation.controllers.components.TaskListController;
import com.smarttask.manager.presentation.controllers.components.modalDialog.AddProjectController;
import com.smarttask.manager.presentation.controllers.components.modalDialog.ScoreInsightController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class InboxController implements Initializable {

    @FXML private AddTaskController addTaskSectionController;
    @FXML private TaskListController taskListSectionController;
    @FXML private Button reportInsightBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (taskListSectionController != null) {
            taskListSectionController.loadTasks();
        }

        if (addTaskSectionController != null && taskListSectionController != null) {

            addTaskSectionController.setOnTaskAdded(() -> {
                System.out.println("🔄 Dashboard: Refreshing Task List...");
                taskListSectionController.loadTasks();
            });

        }
    }

    @FXML
    private void openProductivityInsights() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/ScoreInsight.fxml"));
            Parent root = loader.load();

            ScoreInsightController controller = loader.getController();

            GenerateProductivityReportUseCase useCase = new GenerateProductivityReportUseCase();

            // TODO: Replace "current-user-id" with the actual logged-in user's ID
            String currentUserId = null;
            if (UserSession.getInstance().getUser() != null) {
                currentUserId = UserSession.getInstance().getUser().id();
            } else {
                throw new RuntimeException("Utilisateur non connecté !");
            }

            ProductivityReport report = useCase.execute(currentUserId);

            controller.setReportData(report);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            Stage ownerStage = (Stage) reportInsightBtn.getScene().getWindow();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setX(ownerStage.getX());
            stage.setY(ownerStage.getY());
            stage.setWidth(ownerStage.getWidth());
            stage.setHeight(ownerStage.getHeight());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException | SQLException e) {
            System.err.println("Error Opening Productivity Insights: " + e.getMessage());
        }
    }
}
