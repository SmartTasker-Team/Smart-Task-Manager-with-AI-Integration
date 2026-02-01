package com.smarttask.manager.domain.model;

import java.time.LocalDateTime;

public class Comment {
    private String idComment;
    private String content;
    private LocalDateTime createdAt;
    private String taskId;
    private String userId;

    public Comment(String idComment, String content, String taskId, String userId) {
        this.idComment = idComment;
        this.content = content;
        this.taskId = taskId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public String getIdComment(){ return idComment;};
    public String getContent(){ return content;};
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getuserId(){ return userId;};
    public String getTaskId(){ return taskId;};

    public void setContent(String content){
        this.content = content;
    };

    public String toString() {
        return "Task{" +
                "idComment='" + idComment + '\'' +
                ", content='" + content + '\'' +
                ", userId='" + userId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public static void main(String[] args){
        Comment c1 = new Comment("33333","hhhhhhhhhhhhh","333333","3333333");
        System.out.println(c1);
    };

}
