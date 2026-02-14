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

    private GetAnalyticsDataUseCase analyticsService;

    @FXML
    public void initialize() {
        CategoryAxis xAxis = (CategoryAxis) categoryBarChart.getXAxis();

        xAxis.setTickLabelRotation(45);
        xAxis.setTickMarkVisible(true);

        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        try {
            if (this.analyticsService == null) {
                this.analyticsService = new GetAnalyticsDataUseCase();
            }

            String currentUserId = "user123";
            if (UserSession.getInstance().getUser() != null) {
                currentUserId = UserSession.getInstance().getUser().id();
            }


            final AnalyticsSummary summary = analyticsService.execute(currentUserId);

            System.out.println("📊 UI Loading data for: " + currentUserId);
            System.out.println("📊 Status items found: " + summary.tasksByStatus.size());

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

        xAxis.setTickLabelRotation(0);
        xAxis.setTickLabelGap(10);
        xAxis.setAutoRanging(true);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tasks");

        if (summary.tasksByCategory != null) {
            summary.tasksByCategory.forEach((category, count) -> {
                xAxis.getCategories().add(category);
                series.getData().add(new XYChart.Data<>(category, count));
            });
        }

        categoryBarChart.getData().add(series);

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