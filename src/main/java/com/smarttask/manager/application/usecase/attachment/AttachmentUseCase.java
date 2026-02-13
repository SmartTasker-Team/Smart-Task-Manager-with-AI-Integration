package com.smarttask.manager.application.usecase.attachment;

import com.smarttask.manager.domain.model.Attachment;
import com.smarttask.manager.domain.repository.AttachmentRepository;
import java.util.UUID;

public class AttachmentUseCase {
    private final AttachmentRepository repository;

    public AttachmentUseCase(AttachmentRepository repository) {
        this.repository = repository;
    }

    /**
     * Scenario: User uploads a file.
     * In a real system, the file is sent to S3/Server first, then we call this.
     */
    public String attachFileToComment(String commentId, String url, String type) {
        Attachment attachment = new Attachment(
                UUID.randomUUID().toString(),
                commentId,
                url,
                type
        );
        repository.save(attachment);
        return attachment.getIdAttachment();
    }

    public void removeAttachment(String id) {
        repository.delete(id);
    }
}