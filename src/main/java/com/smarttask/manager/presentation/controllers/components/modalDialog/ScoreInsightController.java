package com.smarttask.manager.presentation.controllers.components.modalDialog;

import com.smarttask.manager.application.dto.ProductivityReport;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ScoreInsightController {

    @FXML private Button btnCancel;
    @FXML private Label lblScoreValue;
    @FXML private Label lblScoreTitle;
    @FXML private Circle scoreProgress;
    @FXML private Text txtAnalysis;
    @FXML private Label lblSuggestion;

    private static final double CIRCUMFERENCE = 2 * Math.PI * 45;

    @FXML
    public void initialize() {
        // Reset state
        scoreProgress.getStrokeDashArray().setAll(CIRCUMFERENCE);
        scoreProgress.setStrokeDashOffset(CIRCUMFERENCE);
    }

    public void setReportData(ProductivityReport report) {
        if (report == null) return;

        lblScoreValue.setText(String.valueOf(report.getScore()));
        txtAnalysis.setText(report.getSummary());
        lblSuggestion.setText(report.getSuggestion());

        animateScore(report.getScore());

        updateVisualContext(report.getScore());
    }

    private void animateScore(int score) {
        double offset = CIRCUMFERENCE * (1 - (score / 100.0));

        Platform.runLater(() -> {
            scoreProgress.setStrokeDashOffset(offset);
        });
    }

    private void updateVisualContext(int score) {
        String title;
        Color color;

        if (score >= 80) {
            title = "Excellent Work!";
            color = Color.web("#10b981");
        } else if (score >= 50) {
            title = "On Track";
            color = Color.web("#f59e0b");
        } else {
            title = "Needs Attention";
            color = Color.web("#ef4444");
        }

        lblScoreTitle.setText(title);
        scoreProgress.setStroke(color);
        lblScoreTitle.setTextFill(color);
    }

    @FXML
    private void handleCancel() {
        if (btnCancel != null && btnCancel.getScene() != null) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }
}