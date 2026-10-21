package com.smarttask.manager.application.usecase.analytics;

import com.smarttask.manager.application.dto.AnalyticsSummary;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.domain.repository.TaskRepository;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAnalyticsDataUseCase {
    private final TaskRepository repository;
    Connection conn = DatabaseConnection.getConnection();

    public GetAnalyticsDataUseCase() throws SQLException {
        this.repository = new PostgresTaskRepository(conn);
    }

    public AnalyticsSummary execute(String ownerId) {
        List<Task> tasks = repository.findByOwner(ownerId);

        Map<String, Integer> statusCount = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Integer> dailyTrend = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (Task t : tasks) {

            String status = t.getStatus().toString();
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);

            String cat = (t.getCategory() == null) ? "Uncategorized" : t.getCategory();
            categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);

            if ("DONE".equals(t.getStatus().toString())) {
                String dateKey = t.getCompletedAt().format(formatter);
                dailyTrend.put(dateKey, dailyTrend.getOrDefault(dateKey, 0) + 1);
            }
        }
        return new AnalyticsSummary(statusCount, categoryCount, dailyTrend);
    }
}
