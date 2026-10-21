package com.smarttask.manager.application.usecase.comment;

import com.smarttask.manager.domain.model.Comment;
import com.smarttask.manager.domain.repository.CommentRepository;
import com.smarttask.manager.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.UUID;

public class CommentUseCase {
    private final CommentRepository repository;

    public CommentUseCase(CommentRepository repository) { this.repository = repository; }

    // Scenario 1: Posting a new comment
    public String postComment(String taskId, String userId, String content) {
        Comment comment = new Comment(UUID.randomUUID().toString(), content, taskId, userId, LocalDateTime.now());
        repository.save(comment);
        return comment.getIdComment();
    }

    // Scenario 2: Editing a comment with version check
    public void editComment(String commentId, String newContent, String userId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new DomainException("Comment not found."));

        // Security check: only the author can edit
        if (!comment.getUserId().equals(userId)) {
            throw new DomainException("Unauthorized: You are not the author of this comment.");
        }

        comment.updateContent(newContent);
        if (!repository.update(comment)) {
            throw new DomainException("Conflict: Comment was modified or deleted by someone else.");
        }
    }

    // Scenario 3: Explicit deletion
    public void removeComment(String commentId, String userId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new DomainException("Comment already deleted."));

        if (!comment.getUserId().equals(userId)) {
            throw new DomainException("Unauthorized.");
        }

        repository.delete(commentId);
    }
}