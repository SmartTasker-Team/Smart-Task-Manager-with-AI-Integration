package com.smarttask.manager.presentation.controllers.components.modalDialog;

import com.smarttask.manager.application.dto.ProjectDTO;
import com.smarttask.manager.application.usecase.project.ProjectUseCase;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresProjectRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddProjectController {

    @FXML private TextField nameField;
    @FXML private TextField descField;
    @FXML private Button addBtn;
    @FXML private Button cancelBtn;

    private ProjectUseCase projectUseCase;
    private Runnable onProjectAddedCallback;

    public void setOnProjectAdded(Runnable callback) {
        this.onProjectAddedCallback = callback;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();
        String desc = descField.getText();

        if (name == null || name.trim().isEmpty()) {
            showAlert("Erreur", "Le nom du projet est obligatoire.");
            return;
        }

        addBtn.setDisable(true);

        new Thread(() -> {
            try {
                if (this.projectUseCase == null) {
                    var connection = DatabaseConnection.getConnection();
                    var repo = new PostgresProjectRepository(connection);
                    this.projectUseCase = new ProjectUseCase(repo);
                }

                String userId = "685f976d-ef7d-4209-bea2-0ec5ffea7571";

                ProjectDTO newProject = new ProjectDTO(
                        name,
                        desc,
                        userId,
                        null
                );

                projectUseCase.createProject(newProject);
                System.out.println("✅ Projet personnel créé : " + name);

                Platform.runLater(() -> {
                    if (onProjectAddedCallback != null) {
                        onProjectAddedCallback.run();
                    }
                    closeDialog();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Erreur", "Impossible de créer le projet : " + e.getMessage());
                    addBtn.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}