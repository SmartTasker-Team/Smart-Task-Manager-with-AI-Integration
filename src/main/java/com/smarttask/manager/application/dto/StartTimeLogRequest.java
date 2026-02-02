package com.smarttask.manager.application.dto;

/**
 * <h2>StartTimeLogRequest</h2>
 * <p>
 * Command DTO used to initiate a new time tracking session.
 * </p>
 */
public record StartTimeLogRequest(
        String taskId,
        String userId
) {}