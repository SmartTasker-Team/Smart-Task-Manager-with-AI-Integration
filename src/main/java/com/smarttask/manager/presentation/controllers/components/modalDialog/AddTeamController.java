package com.smarttask.manager.presentation.controllers.components.modalDialog;

import com.smarttask.manager.application.usecase.team.TeamUseCase;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTeamRepository;
import com.smarttask.manager.infrastructure.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTeamController {
    @FXML private TextField nameField;
    @FXML private Button btnAdd;
    @FXML private Button btnCancel;

    private TeamUseCase teamUseCase;
    private Runnable onTeamAddedCallback;

    public void setOnTeamAdded(Runnable callback) {
        this.onTeamAddedCallback = callback;
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

        if (name == null || name.trim().isEmpty()) {
            showAlert("Erreur", "Le nom de l'équipe est obligatoire.");
            return;
        }

        if (btnAdd != null) btnAdd.setDisable(true);

        new Thread(() -> {
            try {
                if (this.teamUseCase == null) {
                    var connection = DatabaseConnection.getConnection();
                    var repo = new PostgresTeamRepository(connection);
                    this.teamUseCase = new TeamUseCase(repo);
                }

                String currentUserId = null;
                if (UserSession.getInstance().getUser() != null) {
                    currentUserId = UserSession.getInstance().getUser().id();
                } else {
                    throw new RuntimeException("Utilisateur non connecté !");
                }

                String newTeamId = teamUseCase.createTeam(name, currentUserId);
                System.out.println("✅ Équipe créée avec succès : " + name + " (ID: " + newTeamId + ")");

                Platform.runLater(() -> {
                    if (onTeamAddedCallback != null) {
                        onTeamAddedCallback.run();
                    }
                    closeDialog();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Erreur", "Impossible de créer l'équipe : " + e.getMessage());
                    if (btnAdd != null) btnAdd.setDisable(false);
                });
            }
        }).start();
    }

    private void closeDialog() {
        if (nameField != null && nameField.getScene() != null) {
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