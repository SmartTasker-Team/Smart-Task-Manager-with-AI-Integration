package com.smarttask.manager.application.dto;

/**
 * <h2>CreateCommentRequest</h2>
 * <p>
 * Data Transfer Object containing the necessary information to create a new comment.
 * </p>
 *
 * <p>
 * Carries the raw input from the view to the CreateCommentUseCase.
 * </p>

 */
public record CreateCommentRequest(
        String content,
        String taskId,
        String userId
) {}