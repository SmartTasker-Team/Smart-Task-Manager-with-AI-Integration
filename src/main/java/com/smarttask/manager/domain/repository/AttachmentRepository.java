package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Attachment;
import java.util.List;

/**
 * Interface defining the contract for Task Attachment persistence.
 * <p>
 * This repository manages the metadata and references to external files
 * (documents, images, etc.) linked to tasks. It abstracts the underlying
 * storage mechanism, whether it be a remote file server via REST or
 * a local cache.
 * </p>
 */

public interface AttachmentRepository {
    void save(Attachment attachment);
    List<Attachment> findByTaskId(String taskId);
    void remove(String attachmentId);
}