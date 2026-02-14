package com.smarttask.manager.presentation.controllers.pages;

import com.smarttask.manager.application.dto.AnalyticsSummary;
import com.smarttask.manager.application.usecase.analytics.GetAnalyticsDataUseCase;
import com.smarttask.manager.infrastructure.session.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

import java.util.TreeMap;

public class AnalyticsController {

    @FXML private LineChart<String, Number> trendLineChart;
    @FXML private BarChart<String, Number> categoryBarChart;
    @FXML private PieChart statusPieChart;

    // ⚠️ CHANGEMENT ICI : On retire 'final' et l'initialisation directe
    private GetAnalyticsDataUseCase analyticsService;

    @FXML
    public void initialize() {
        CategoryAxis xAxis = (CategoryAxis) categoryBarChart.getXAxis();

        // Force the labels to remain centered under the ticks
        xAxis.setTickLabelRotation(45); // Keep your rotation
        xAxis.setTickMarkVisible(true);

        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        try {
            if (this.analyticsService == null) {
                this.analyticsService = new GetAnalyticsDataUseCase();
            }

            // 1. Get Current User (Using the ID that actually works in your test)
            String currentUserId = "user123";
            if (UserSession.getInstance().getUser() != null) {
                currentUserId = UserSession.getInstance().getUser().id();
            }


            // 2. Fetch Data
            final AnalyticsSummary summary = analyticsService.execute(currentUserId);

            // DEBUG: Check this in your console!
            System.out.println("📊 UI Loading data for: " + currentUserId);
            System.out.println("📊 Status items found: " + summary.tasksByStatus.size());

            // 3. Populate Charts on the UI Thread
            javafx.application.Platform.runLater(() -> {
                populateStatusChart(summary);
                populateCategoryChart(summary);
                populateTrendChart(summary);
            });

        } catch (Exception e) {
            System.err.println("❌ Error processing analytics data:");
            e.printStackTrace();
        }
    }

    // ... (Le reste des méthodes populate reste identique) ...

    private void populateStatusChart(AnalyticsSummary summary) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        if (summary.tasksByStatus != null) {
            summary.tasksByStatus.forEach((status, count) ->
                    pieData.add(new PieChart.Data(status + " (" + count + ")", count)));
        }
        statusPieChart.setData(pieData);
    }


    private void populateCategoryChart(AnalyticsSummary summary) {
        categoryBarChart.getData().clear();

        CategoryAxis xAxis = (CategoryAxis) categoryBarChart.getXAxis();
        xAxis.getCategories().clear();

        // 👇 AJOUTEZ CES LIGNES POUR CORRIGER L'ALIGNEMENT
        xAxis.setTickLabelRotation(0); // Remettre à 0 pour tester, ou 45 si nécessaire
        xAxis.setTickLabelGap(10);     // Espace entre la barre et le texte
        xAxis.setAutoRanging(true);    // Important pour que toutes les catégories s'affichent

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tasks");

        if (summary.tasksByCategory != null) {
            summary.tasksByCategory.forEach((category, count) -> {
                // Add the category name to the axis explicitly
                xAxis.getCategories().add(category);
                series.getData().add(new XYChart.Data<>(category, count));
            });
        }

        categoryBarChart.getData().add(series);

        // Adjust spacing so bars aren't too wide or too thin
        categoryBarChart.setCategoryGap(50);
    }

    private void populateTrendChart(AnalyticsSummary summary) {
        trendLineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Completed Tasks");
        if (summary.completedTasksPerDay != null) {
            new TreeMap<>(summary.completedTasksPerDay).forEach((date, count) ->
                    series.getData().add(new XYChart.Data<>(date, count)));
        }
        trendLineChart.getData().add(series);
    }
}