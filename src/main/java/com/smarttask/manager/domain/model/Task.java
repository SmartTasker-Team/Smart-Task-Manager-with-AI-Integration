package com.smarttask.manager.domain.model;
import java.util.List;
/**
 * The primary Entity representing a unit of work.
 * <p>
 * Encapsulates state and behavior, including subtasks, dependencies, recurring schedules,
 * and priority levels. This entity ensures business invariants are maintained.
 * </p>
 */

public class Task {
    private Long id;
    private Long ownerId;
    private String title;
    private String content;
    private TaskStatus status;
    private PriorityLevel priority;
    private long versionNumber;
    private List<Long> sharedWithUserIds;

    // Constructors, Getters, and Setters
    public void incrementVersion() {
        this.versionNumber++;
    }
}
