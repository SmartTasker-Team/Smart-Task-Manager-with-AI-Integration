package com.smarttask.manager.application.dto;
import java.util.Map;

public class AnalyticsSummary {
    public Map<String, Integer> tasksByStatus;
    public Map<String, Integer> tasksByCategory;
    public Map<String, Integer> completedTasksPerDay;

    public AnalyticsSummary(Map<String, Integer> status, Map<String, Integer> category, Map<String, Integer> trend) {
        this.tasksByStatus = status;
        this.tasksByCategory = category;
        this.completedTasksPerDay = trend;
    }
}