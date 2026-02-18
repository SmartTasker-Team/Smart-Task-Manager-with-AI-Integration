package com.smarttask.manager.presentation.controllers.components.modalDialog;

import com.smarttask.manager.application.dto.ProjectDTO;
import com.smarttask.manager.application.usecase.project.ProjectUseCase;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresProjectRepository;
import com.smarttask.manager.infrastructure.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTeamProjectController {
    @FXML private TextField nameField;
    @FXML private TextField descField;
    @FXML private Button addBtn;
    @FXML private Button cancelBtn;

    private ProjectUseCase projectUseCase;
    private Runnable onProjectAddedCallback;

    private String currentTeamId;

    public void setOnProjectAdded(Runnable callback) {
        this.onProjectAddedCallback = callback;
    }

    public void setTeamId(String teamId) {
        this.currentTeamId = teamId;
        System.out.println("✅ ID d'équipe reçu : " + teamId);
    }

    @FXML
    public void initialize() {
        // Rien de spécial ici
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();
        String desc = descField.getText();

        if (this.currentTeamId == null || this.currentTeamId.isEmpty()) {
            showAlert("Erreur", "Aucune équipe sélectionnée. Impossible de créer le projet.");
            return;
        }

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

                String currentUserId = null;
                if (UserSession.getInstance().getUser() != null) {
                    currentUserId = UserSession.getInstance().getUser().id();
                } else {
                    throw new RuntimeException("Utilisateur non connecté !");
                }

                ProjectDTO newProject = new ProjectDTO(
                        name,
                        desc,
                        currentUserId,
                        this.currentTeamId
                );

                projectUseCase.createProject(newProject);
                System.out.println("✅ Projet créé pour l'équipe ID : " + this.currentTeamId);

                Platform.runLater(() -> {
                    if (onProjectAddedCallback != null) {
                        onProjectAddedCallback.run();
                    }
                    closeDialog();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Erreur", "Échec de la création : " + e.getMessage());
                    addBtn.setDisable(false);
                });
            }
        }).start();

    }

    private void closeDialog() {
        if (cancelBtn != null && cancelBtn.getScene() != null) {
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.close();
        } else if (nameField != null && nameField.getScene() != null) {
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}