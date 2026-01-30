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
    Optional<Task> findById(Long id);
    List<Task> findByOwnerId(Long ownerId);
    Task save(Task task); // Used for both Create and Update
    void delete(Long id);

    // Specific for dual-database sync strategy
    boolean updateIfVersionMatches(Task task, long expectedVersion);
}
