package com.smarttask.manager.application.dto;

import java.time.LocalDateTime;

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