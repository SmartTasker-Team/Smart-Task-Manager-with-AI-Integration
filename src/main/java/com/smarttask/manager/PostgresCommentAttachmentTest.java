package com.smarttask.manager;

import com.smarttask.manager.domain.model.*;
import com.smarttask.manager.infrastructure.persistence.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.UUID;

public class PostgresCommentAttachmentTest {

    public static void main(String[] args) {
        System.out.println("=== Starting Comment & Attachment Integration Test ===");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Setup Repositories
            PostgresUserRepository userRepo = new PostgresUserRepository(conn);
            PostgresTaskRepository taskRepo = new PostgresTaskRepository(conn);
            PostgresCommentRepository commentRepo = new PostgresCommentRepository(conn);
            PostgresAttachmentRepository attachRepo = new PostgresAttachmentRepository(conn);

            // 2. Setup Prerequisites (User and Task)
            String userId = UUID.randomUUID().toString();
            userRepo.save(new User(userId, "Tester", "test2@attach.com", "HASHED_Secure123"));

            String taskId = UUID.randomUUID().toString();
            taskRepo.save(new Task(taskId, "Task with attachments", userId, LocalDateTime.now()));

            // 3. STEP 1: Create a Comment
            String commentId = UUID.randomUUID().toString();
            Comment comment = new Comment(commentId, "Check the attached file!", taskId, userId, LocalDateTime.now());
            commentRepo.save(comment);
            System.out.println("Comment saved.");

            // 4. STEP 2: Create an Attachment linked to that Comment
            String attachId = UUID.randomUUID().toString();
            Attachment attachment = new Attachment(attachId, commentId, "https://storage.cloud/logs.txt", "text/plain");
            attachRepo.save(attachment);
            System.out.println("Attachment saved and linked to comment.");

            // 5. STEP 3: Verify Retrieval
            System.out.println("\n--- Verifying Data Structure ---");
            if (attachRepo.findById(attachId).isPresent()) {
                System.out.println("SUCCESS: Attachment is present in DB.");
            }

            // 6. STEP 4: Test CASCADE DELETE (Deleting Comment)
            System.out.println("\n--- Testing Cascade: Deleting Comment ---");
            commentRepo.delete(commentId);

            // Check if attachment was automatically deleted by Postgres
            boolean attachStillExists = attachRepo.findById(attachId).isPresent();
            if (!attachStillExists) {
                System.out.println("SUCCESS: Attachment was automatically deleted by Cascade.");
            } else {
                System.err.println("FAILURE: Attachment orphaned! Check SQL 'ON DELETE CASCADE'.");
            }

            // 7. STEP 5: Test Task Level Cascade
            System.out.println("\n--- Testing Cascade: Deleting Task ---");
            // Re-create comment and attachment first
            commentRepo.save(comment);
            attachRepo.save(attachment);

            taskRepo.delete(taskId); // Deleting the task should wipe everything

            if (commentRepo.findById(commentId).isEmpty() && attachRepo.findById(attachId).isEmpty()) {
                System.out.println("SUCCESS: Task deletion purged all related comments and files.");
            }

            // Cleanup user
            userRepo.delete(userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}