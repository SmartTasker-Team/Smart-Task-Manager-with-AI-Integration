package com.smarttask.manager.domain.model;

/**
 * Enumeration defining the possible lifecycle states of a task.
 * <p>
 * Standardizes status values (e.g., Pending, In Progress, Completed) for consistency
 * across the analytics dashboard and visual progress tracking.
 * </p>
 */

public enum TaskStatus {
    TODO, DOING, DONE, ARCHIVED;

    public static TaskStatus fromString(String status) {
        try {
            return TaskStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TODO;
        }
    }
}