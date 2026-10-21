package com.smarttask.manager.domain.model;

/**
 * Enumération représentant les niveaux d'accès.
 * En tant que fichier autonome, elle peut être réutilisée par d'autres entités
 * (ex: ProjectAssignment) et contenir de la logique métier.
 */
public enum PermissionLevel {
    VIEWER,
    EDITOR,
    ADMIN;

    public boolean canEdit() {
        return this == EDITOR || this == ADMIN;
    }

    /**
     * Vérifie si le niveau permet la suppression (Admin seulement).
     */
    public boolean canDelete() {
        return this == ADMIN;
    }
}