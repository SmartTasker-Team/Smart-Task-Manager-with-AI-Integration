package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Attachment;
import java.util.List;
import java.util.Optional;

public interface AttachmentRepository {
    void save(Attachment attachment);
    void delete(String idAttachment);
    Optional<Attachment> findById(String idAttachment);
    List<Attachment> findByCommentId(String commentId);
}