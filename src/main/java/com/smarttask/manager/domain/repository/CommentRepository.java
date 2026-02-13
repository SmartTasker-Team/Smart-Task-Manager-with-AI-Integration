package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Comment;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining the contract for Comment persistence.
 * <p>
 * This repository allows the Domain layer to manage task-related discussions
 * without being coupled to specific database technologies (PostgreSQL/SQLite).
 * It supports our collaboration module by handling comment lifecycle and retrieval.
 * </p>
 */

public interface CommentRepository {
    void save(Comment comment);
    boolean update(Comment comment); // Scenario: Editing a comment
    void delete(String idComment);   // Scenario: Deleting a comment
    Optional<Comment> findById(String idComment);
    List<Comment> findByTaskId(String taskId);
}