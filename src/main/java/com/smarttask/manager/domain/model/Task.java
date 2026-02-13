package com.smarttask.manager.domain.model;

import com.smarttask.manager.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Task {
    // Identity & Metadata (Final)
    private final String idTask;
    private final String ownerId;
    private final LocalDateTime createdAt;

    // State Attributes
    private String title;
    private String description;
    private String category;
    private PriorityLevel priority;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt; // NEW ATTRIBUTE
    private boolean isRecurring;
    private RecurrenceType recurrenceType;
    private String parentTaskId;
    private long versionNumber;
    private String projectId;

    // Aggregate Relationships
    private final List<Task> subtasks = new ArrayList<>();

    /**
     * Constructor for creating a NEW task.
     */
    public Task(String idTask, String title, String ownerId, LocalDateTime createdAt) {
        this.idTask = Objects.requireNonNull(idTask);
        this.title = Objects.requireNonNull(title);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.createdAt = createdAt;
        this.status = TaskStatus.TODO;
        this.priority = PriorityLevel.URGENT_NOT_IMPORTANT;
        this.versionNumber = 1; // Initial version
    }

    // --- Business Logic Methods ---

    public boolean isOverdue(LocalDateTime referenceTime) {
        if (status == TaskStatus.DONE || status == TaskStatus.ARCHIVED || dueDate == null) {
            return false;
        }
        return dueDate.isBefore(referenceTime);
    }

    public void complete() {
        if (hasActiveSubtasks()) {
            throw new DomainException("Cannot complete: Subtasks are still active.");
        }
        if (this.status != TaskStatus.DONE) {
            this.status = TaskStatus.DONE;
            this.completedAt = LocalDateTime.now(); // SET AUTOMATICALLY
            this.versionNumber++;
        }
    }
    /**
     * Logic: If a task is reopened, the completion date must be cleared.
     */
    public void reopen() {
        if (this.status == TaskStatus.DONE) {
            this.status = TaskStatus.TODO;
            this.completedAt = null; // CLEAR AUTOMATICALLY
            this.versionNumber++;
        }
    }

    private boolean hasActiveSubtasks() {
        return subtasks.stream().anyMatch(s -> s.getStatus() != TaskStatus.DONE && s.getStatus() != TaskStatus.ARCHIVED);
    }

    public void addSubtask(Task subtask) {
        if (subtask.getIdTask().equals(this.idTask)) throw new DomainException("Self-dependency error.");
        subtask.parentTaskId = this.idTask;
        this.subtasks.add(subtask);
        this.versionNumber++;
    }

    /**
     * Updates details and increments version for synchronization logic.
     */
    public void updateDetails(String title, String description, String category,
                              PriorityLevel priority, LocalDateTime dueDate,
                              boolean isRecurring, RecurrenceType recurrenceType, String projectId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isRecurring = isRecurring;
        this.recurrenceType = recurrenceType;
        this.projectId = projectId;
        this.versionNumber++;
    }

    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void loadVersion(long version) { this.versionNumber = version; }
    public void setStatus(TaskStatus status) { this.status = status; }

    // --- Getters ---
    public String getIdTask() { return idTask; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public PriorityLevel getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public boolean isRecurring() { return isRecurring; }
    public RecurrenceType getRecurrenceType() { return recurrenceType; }
    public String getParentTaskId() { return parentTaskId; }
    public long getVersionNumber() { return versionNumber; }
    public String getOwnerId() { return ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getProjectId() { return projectId; }
    public List<Task> getSubtasks() { return Collections.unmodifiableList(subtasks); }
}