package com.smarttask.manager.domain.model;

import com.smarttask.manager.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {
    private final String idComment;
    private final String taskId;
    private final String userId;
    private final LocalDateTime createdAt;

    private String content;
    private long versionNumber;

    public Comment(String idComment, String content, String taskId, String userId, LocalDateTime createdAt) {
        this.idComment = Objects.requireNonNull(idComment);
        this.taskId = Objects.requireNonNull(taskId);
        this.userId = Objects.requireNonNull(userId);
        this.createdAt = createdAt;
        this.versionNumber = 1;
        updateContent(content); // Use logic method
    }

    public void updateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new DomainException("Comment content cannot be empty.");
        }
        if (content.length() > 2000) {
            throw new DomainException("Comment is too long (max 2000 chars).");
        }
        this.content = content;
        this.versionNumber++;
    }

    public void loadVersion(long version) { this.versionNumber = version; }

    public String getIdComment() { return idComment; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getTaskId() { return taskId; }
    public String getUserId() { return userId; }
    public long getVersionNumber() { return versionNumber; }
}