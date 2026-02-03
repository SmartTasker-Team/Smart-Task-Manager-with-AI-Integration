package com.smarttask.manager.application.dto;

import java.time.LocalDateTime;

/**
 * <h2>TaskAssignmentDTO</h2>
 * <p>
 * Data Transfer Object representing the link between a Task and a User.
 * </p>
**/
public record TaskAssignmentDTO(
        String taskId,
        String userId,
        String username,
        LocalDateTime assignedAt
) {}