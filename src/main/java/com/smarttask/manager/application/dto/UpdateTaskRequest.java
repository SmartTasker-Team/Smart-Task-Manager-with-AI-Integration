package com.smarttask.manager.application.dto;

import java.time.LocalDateTime;
/**
 * Data Transfer Object (DTO) used to request an update to an existing task.
 * <p>
 * This record encapsulates the changes intended by the user and includes
 * versioning information to support the project's "Last Write Wins" (LWW)
 * conflict resolution strategy.
 * </p>
 **/

public record UpdateTaskRequest(
        String id,
        String title,
        String description,
        String category,
        String priority,
        LocalDateTime dueDate,
        boolean isRecurring,
        String recurrenceRule,
        long currentVersion // Utilisé pour le "Last Write Wins"
) {}