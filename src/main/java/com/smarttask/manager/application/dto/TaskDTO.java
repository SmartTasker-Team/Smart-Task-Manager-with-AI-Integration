package com.smarttask.manager.application.dto;

import com.smarttask.manager.domain.model.PriorityLevel;
import com.smarttask.manager.domain.model.RecurrenceType;
import com.smarttask.manager.domain.model.TaskStatus;

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
        String title, String description, String category,
        PriorityLevel priority, TaskStatus status, LocalDateTime dueDate, LocalDateTime completed_at,
        boolean isRecurring, RecurrenceType recurrenceType,
        String ownerId, String projectId
) {}




