package com.smarttask.manager.application.dto;
import java.time.LocalDate;

/**
 * Data Transfer Object representing the result of an AI analysis operation.
 * <p>
 * This object encapsulates the structured data returned by the Cloud AI service after
 * parsing natural language or analyzing productivity patterns.
 * </p>
 */
public class AiInsightResult {
    public String title;
    public LocalDate date;
    public String priority; // "HIGH", "MEDIUM", "LOW"
    public String category;

    // Empty constructor for JSON parsers
    public AiInsightResult() {}

    public AiInsightResult(String title, LocalDate date, String priority, String category) {
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("Task: %s | Date: %s | Priority: %s", title, date, priority);
    }
}