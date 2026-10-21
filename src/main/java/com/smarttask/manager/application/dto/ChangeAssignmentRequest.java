package com.smarttask.manager.application.dto;

/**
 * <h2>ChangeAssignmentRequest</h2>
 * <p>
 * Data Transfer Object used to add or remove a user from a task.
 * </p>
**/
public record ChangeAssignmentRequest(
        String taskId,
        String userId,
        boolean isAdding
) {}