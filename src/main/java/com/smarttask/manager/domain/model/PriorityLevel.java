package com.smarttask.manager.domain.model;

/**
 * Enum representing the urgency and importance of a task.
 * <p>
 * Used to implement the Eisenhower priority matrix method for task organization.
 * </p>
 */

public enum PriorityLevel {

    URGENT_IMPORTANT(true, true),
    NOT_URGENT_IMPORTANT(false, true),
    URGENT_NOT_IMPORTANT(true, false),
    NOT_URGENT_NOT_IMPORTANT(false, false);

    private final boolean urgent;
    private final boolean important;

    PriorityLevel(boolean urgent, boolean important) {
        this.urgent = urgent;
        this.important = important;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public boolean isImportant() {
        return important;
    }
}




/*public enum PriorityLevel {
    LOW, MEDIUM, HIGH;

    public static PriorityLevel fromString(String priority) {
        try {
            return PriorityLevel.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LOW; // Valeur par défaut
        }
    }
}

 */
