package com.smarttask.manager.domain.model;

import com.smarttask.manager.domain.exception.DomainException;
import java.util.Objects;

public class Attachment {
    private final String idAttachment;
    private final String commentId; // Attached to a comment
    private String storageUrl;
    private String fileType;
    private long versionNumber;

    public Attachment(String idAttachment, String commentId, String storageUrl, String fileType) {
        this.idAttachment = Objects.requireNonNull(idAttachment);
        this.commentId = Objects.requireNonNull(commentId);
        this.versionNumber = 1;

        setStorageUrl(storageUrl);
        setFileType(fileType);
    }

    public void setStorageUrl(String storageUrl) {
        if (storageUrl == null || !storageUrl.startsWith("http")) {
            throw new DomainException("Invalid storage URL. Must be a valid link.");
        }
        this.storageUrl = storageUrl;
        this.versionNumber++;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
        this.versionNumber++;
    }

    // Infrastructure hook for DB loading
    public void loadVersion(long version) { this.versionNumber = version; }

    public String getIdAttachment() { return idAttachment; }
    public String getCommentId() { return commentId; }
    public String getStorageUrl() { return storageUrl; }
    public String getFileType() { return fileType; }
    public long getVersionNumber() { return versionNumber; }
}