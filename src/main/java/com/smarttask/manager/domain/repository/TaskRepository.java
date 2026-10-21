package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Task;
import java.util.List;
import java.util.Optional;

/**
 * Domain interface for Task persistence.
 * This layer is independent of any SQL or Framework.
 */
public interface TaskRepository {
    void save(Task task);
    boolean update(Task task); // Conflict resolution via boolean return
    void delete(String idTask);
    Optional<Task> findById(String idTask);
    List<Task> findByOwner(String ownerId);
}