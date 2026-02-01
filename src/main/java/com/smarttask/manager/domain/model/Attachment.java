package com.smarttask.manager.domain.model;

import java.util.UUID;

/**
 * Entité du domaine représentant une pièce jointe.
 * L'URL pointe vers le stockage serveur indexé par PostgreSQL.
 */

public class Attachment {
    private String idAttachment;
    private String taskId;
    private String storageUrl;
    private String fileType;

    public Attachment(String idAttachment, String taskId, String storageUrl, String fileType) {
        this.idAttachment = idAttachment;
        this.taskId = taskId;
        this.storageUrl = storageUrl;
        this.fileType = fileType;
    }

    public String getIdAttachment() { return idAttachment; }
    public String getTaskId() { return taskId; }
    public String getStorageUrl() { return storageUrl; }
    public String getFileType() { return fileType; }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}