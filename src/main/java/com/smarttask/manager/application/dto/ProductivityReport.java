package com.smarttask.manager.application.dto;

/**
 * Data Transfer Object containing aggregated productivity metrics.
 * <p>
 * Used to transport analysis data such as work patterns, time tracking stats, and
 * performance trends to the Analytics Dashboard.
 * </p>
 */
public class ProductivityReport {
    public int score;
    public String summary;
    public String suggestion;

    public ProductivityReport() {}

    public ProductivityReport(int score, String summary, String suggestion) {
        this.score = score;
        this.summary = summary;
        this.suggestion = suggestion;
    }

    @Override
    public String toString() {
        return String.format("Score: %d | Summary: %s | Suggestion: %s", score, summary, suggestion);
    }
}
