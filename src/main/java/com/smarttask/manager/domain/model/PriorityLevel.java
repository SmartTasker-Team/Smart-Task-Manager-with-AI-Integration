package com.smarttask.manager.domain.model;

/**
 * Enum representing the urgency and importance of a task.
 * <p>
 * Used to implement the Eisenhower priority matrix method for task organization.
 * </p>
 */
public enum PriorityLevel {
    URGENT_IMPORTANT,   // Do First
    NOT_URGENT_IMPORTANT, // Schedule
    URGENT_NOT_IMPORTANT, // Delegate
    NOT_URGENT_NOT_IMPORTANT // Eliminate
}
