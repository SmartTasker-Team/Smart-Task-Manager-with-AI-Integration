package com.smarttask.manager.application.dto;

import java.time.LocalDateTime;
/**
 * Objet de transfert de données (DTO) utilisé pour capturer l'intention de création d'une tâche.
 * <p>
 * Ce record agit comme un objet "Command", transportant toutes les données nécessaires
 * de la couche Présentation vers la couche Application. Il est immuable par conception
 * et garantit que le Cas d'Utilisation (Use Case) reçoit un ensemble complet de données
 * pour instancier une entité Task valide.
 * </p>
 **/

public record CreateTaskRequest(
        String title,
        String description,
        String category,
        String priority,
        LocalDateTime dueDate,
        String ownerId,
        String parentTaskId,
        boolean isRecurring,
        String recurrenceRule
) {}