package com.smarttask.manager.application.dto;

import java.time.LocalDateTime;

/**
 * <h2>TimeLogDTO</h2>
 * <p>
 * Data Transfer Object representing a recorded time interval for a specific task.
 * </p>
 *
 * <p>
 * This DTO provides a read-only view of time tracking data, including an optional
 * task title to facilitate direct display in history lists or dashboards.
 * </p>
 */
public record TimeLogDTO(
        String id,
        String taskId,
        String taskTitle,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long durationSeconds
) {}