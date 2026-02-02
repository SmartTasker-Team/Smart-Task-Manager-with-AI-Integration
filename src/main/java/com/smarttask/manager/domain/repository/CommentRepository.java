package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Comment;
import java.util.List;

/**
 * Interface defining the contract for Comment persistence.
 * <p>
 * This repository allows the Domain layer to manage task-related discussions
 * without being coupled to specific database technologies (PostgreSQL/SQLite).
 * It supports our collaboration module by handling comment lifecycle and retrieval.
 * </p>
*/

public interface CommentRepository {
    void add(Comment comment);
    List<Comment> findByTaskId(String taskId);
    void delete(String id);
}