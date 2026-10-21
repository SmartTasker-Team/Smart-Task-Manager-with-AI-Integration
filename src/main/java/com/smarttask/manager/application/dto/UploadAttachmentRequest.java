package com.smarttask.manager.application.dto;

/**
 * <h2>UploadAttachmentRequest</h2>
 * <p>
 * Data Transfer Object used to request the upload and attachment of a new file.
 * </p>
 **/
public record UploadAttachmentRequest(
        String taskId,
        String rawFileName,
        String contentType,
        byte[] fileData
) {}