package com.smarttask.manager.application.dto;

/**
 * <h2>AttachmentDTO</h2>
 * <p>
 * Data Transfer Object representing a file attachment associated with a task.
 * </p>
**/
public record AttachmentDTO(
        String id,
        String taskId,
        String storageUrl,
        String fileType,
        String fileName
) {}