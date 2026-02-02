package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining the contract for Task persistence.
 * <p>
 * Follows the Dependency Inversion Principle, allowing the Domain layer to remain agnostic
 * of the underlying PostgreSQL implementation.
 * </p>
 */
public interface TaskRepository {
    void save(Task task);
    Optional<Task> findById(String id);
    List<Task> findAllByOwner(String ownerId);
    List<Task> findAllByAssignedUser(String userId);
    void delete(String id);
    long getRemoteVersion(String taskId); // Pour la résolution de conflits
}
