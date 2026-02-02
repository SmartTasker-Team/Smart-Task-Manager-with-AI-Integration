package com.smarttask.manager.application.dto;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Data Transfer Object representing a Task for the UI or Network layers.
 * <p>
 * Acts as a simple data container to facilitate Real-time task sharing between users on the
 * same network without exposing the core {@code Task} domain entity directly.
 * </p>
 */
public record TaskDTO(
        String id,
        String title,
        String description,
        String category,
        String priority,
        String status,
        LocalDateTime dueDate,
        boolean isRecurring,
        String recurrenceRule,
        String ownerId,
        String parentTaskId,
        long versionNumber,
        List<TaskDTO> subtasks // On permet une structure récursive pour l'affichage en arbre
) {}
