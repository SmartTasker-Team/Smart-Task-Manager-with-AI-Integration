package com.smarttask.manager.domain.model;

import com.smarttask.manager.domain.exception.DomainException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class TimeLog {
    private final String idTimeLog;
    private final String taskId;
    private final LocalDateTime startTime;

    private LocalDateTime endTime;
    private long durationSeconds;
    private long versionNumber;

    public TimeLog(String idTimeLog, String taskId, LocalDateTime startTime) {
        this.idTimeLog = Objects.requireNonNull(idTimeLog);
        this.taskId = Objects.requireNonNull(taskId);
        this.startTime = Objects.requireNonNull(startTime);
        this.durationSeconds = 0;
        this.versionNumber = 1;
    }

    /**
     * Business Logic: Stops the timer and calculates duration.
     */
    public void stopLog(LocalDateTime endTime) {
        if (this.endTime != null) {
            throw new DomainException("TimeLog is already stopped.");
        }
        if (endTime.isBefore(this.startTime)) {
            throw new DomainException("End time cannot be before start time.");
        }

        this.endTime = endTime;
        this.durationSeconds = Duration.between(startTime, endTime).getSeconds();
        this.versionNumber++;
    }

    // --- INFRASTRUCTURE HOOKS ---
    public void loadVersion(long version) { this.versionNumber = version; }
    public void setDurationSeconds(long seconds) { this.durationSeconds = seconds; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    // --- GETTERS ---
    public String getIdTimeLog() { return idTimeLog; }
    public String getTaskId() { return taskId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public long getDurationSeconds() { return durationSeconds; }
    public long getVersionNumber() { return versionNumber; }
}