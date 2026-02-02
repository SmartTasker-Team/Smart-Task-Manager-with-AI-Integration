package com.smarttask.manager.application.dto;

import java.time.LocalDateTime;

public record CreateTaskRequest(
        String title,
        String description,
        String category,
        String priority,
        LocalDateTime dueDate,
        String ownerId,
        String parentTaskId,
        boolean isRecurring,
        String recurrenceRule
) {}