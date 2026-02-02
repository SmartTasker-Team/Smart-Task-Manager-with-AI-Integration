package com.smarttask.manager.application.dto;

import java.time.LocalDateTime;

/**
 * <h2>CommentDTO</h2>
 * <p>
 * Data Transfer Object representing a comment for read and display purposes.
 * </p>
 * * <p>
 * This DTO is used to send comment data to the Presentation layer (JavaFX).
 * It includes the author's username to allow direct display without additional
 * user lookups by the UI components.
 * </p>
 */
public record CommentDTO(
        String id,
        String content,
        LocalDateTime createdAt,
        String taskId,
        String userId,
        String username
) {}