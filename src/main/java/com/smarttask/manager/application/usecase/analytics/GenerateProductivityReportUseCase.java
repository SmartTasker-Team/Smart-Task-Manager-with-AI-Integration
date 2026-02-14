package com.smarttask.manager.application.usecase.analytics;
import com.smarttask.manager.application.dto.ProductivityReport;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.domain.repository.TaskRepository;
import com.smarttask.manager.infrastructure.external.ai.GeminiAdapter;
import com.google.gson.Gson;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/**
 * Use Case responsible for generating comprehensive productivity insights.
 * <p>
 * Orchestrates the retrieval of time logs and task completion data to analyze work patterns
 * and suggest optimizations to the user.
 * </p>
 */
public class GenerateProductivityReportUseCase {
    private final GeminiAdapter adapter;
    private final Gson gson;
    private final TaskRepository repository;
    Connection conn = DatabaseConnection.getConnection();

    public GenerateProductivityReportUseCase() throws SQLException {
        this.adapter = new GeminiAdapter();
        this.gson = new Gson();
        this.repository = new PostgresTaskRepository(conn);
    }

    public ProductivityReport execute(String ownerId) {
        List<Task> tasks = repository.findByOwner(ownerId);

        if (tasks.isEmpty() ) {
            return new ProductivityReport(0, "No history found.", "Start adding tasks!");
        }
        StringBuilder summary = new StringBuilder();
        int doneCount = 0;

        for (Task t : tasks) {
            summary.append(String.format("- %s [%s] (Status: %s)\n",
                    t.getTitle(), t.getCategory(), t.getStatus()));

            if ("DONE".equals(t.getStatus())) doneCount++;
        }

        summary.append("\nTotal Tasks: ").append(tasks.size());
        summary.append("\nCompleted: ").append(doneCount);

        String jsonResponse = adapter.analyzeProductivity(summary.toString());

        if (jsonResponse != null) {
            try {
                String cleanJson = jsonResponse
                        .replace("```json", "")
                        .replace("```", "")
                        .trim();

                return gson.fromJson(cleanJson, ProductivityReport.class);

            } catch (Exception e) {
                System.err.println("JSON Parsing Error: " + e.getMessage());
                System.err.println("Raw String was: " + jsonResponse);
            }
        }

        return new ProductivityReport(50, "AI unavailable.", "Keep working hard!");
    }


}
