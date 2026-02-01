package com.smarttask.manager.domain.model;

import java.time.LocalDateTime;

/**
 * Entité du domaine représentant l'assignation d'une tâche à un utilisateur.
 * Cette classe gère les niveaux de permission au sein de la couche Domain.
 */
public class TaskAssignment {
    private String taskAid;
    private String taskId;
    private String userId;
    private PermissionLevel permissionLevel;
    private LocalDateTime assignedAt;

    public TaskAssignment(String taskAid, String taskId, String userId, PermissionLevel permissionLevel, LocalDateTime assignedAt) {
        this.taskAid = taskAid;
        this.taskId = taskId;
        this.userId = userId;
        this.permissionLevel = permissionLevel;
        this.assignedAt = assignedAt;
    }

    // Getters
    public String getTaskAid() { return taskAid; }
    public String getTaskId() { return taskId; }
    public String getUserId() { return userId; }
    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public LocalDateTime getAssignedAt() { return assignedAt; }

    /**
     * Enumération interne pour définir les niveaux d'accès sans dépendre
     * d'une configuration externe.
     */

    public boolean canUserEdit() {
        return permissionLevel.canEdit();
    }

    public boolean canUserDelete() {
        return permissionLevel.canDelete();
    }
}