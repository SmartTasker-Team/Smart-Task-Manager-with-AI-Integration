package com.smarttask.manager.application.dto;

/**
 * <h2>UpdateCommentRequest</h2>
 * <p>
 * Data Transfer Object used to request a modification of an existing comment.
 * </p>
 */
public record UpdateCommentRequest(
        String idComment,
        String content
) {}