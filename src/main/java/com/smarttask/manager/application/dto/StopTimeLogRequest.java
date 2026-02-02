package com.smarttask.manager.application.dto;

/**
 * <h2>StopTimeLogRequest</h2>
 * <p>
 * Command DTO used to finalize an active time tracking session.
 * </p>
 */
public record StopTimeLogRequest(
        String idTimeLog
) {}