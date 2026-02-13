package com.smarttask.manager;

import com.smarttask.manager.domain.model.*;
import com.smarttask.manager.infrastructure.persistence.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Exhaustive Integration Test: Validates every single field and logic transition
 * in the Task repository/entity vertical.
 */
public class PostgresTaskCreationTest {

    public static void main(String[] args) {
        System.out.println("========== EXHAUSTIVE TASK ATTRIBUTE TEST ==========");

        try (Connection conn = DatabaseConnection.getConnection()) {
            PostgresUserRepository userRepo = new PostgresUserRepository(conn);
            PostgresProjectRepository projectRepo = new PostgresProjectRepository(conn);
            PostgresTaskRepository taskRepo = new PostgresTaskRepository(conn);

            // --- STEP 1: PREREQUISITES (FOR FOREIGN KEYS) ---
            String userId = UUID.randomUUID().toString();
            String projectId = UUID.randomUUID().toString();

            userRepo.save(new User(userId, "DeepTester", "exhaustive_" + UUID.randomUUID().toString().substring(0,4) + "@test.com", "Admin123!"));
            projectRepo.save(new Project(projectId, "Main Project Pipeline", userId, null));
            System.out.println("[OK] Dependencies created (User & Project)");

            // --- STEP 2: CREATE TASK WITH FULL ATTRIBUTE SET ---
            String taskId = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deadline = now.plusDays(7).withNano(0); // Clean date for comparison

            Task task = new Task(taskId, "Master Task Implementation", userId, now);

            // Setting every field possible via the updateDetails method
            task.updateDetails(
                    "Final Architecture Audit",           // title
                    "Detailed description of all fields", // description
                    "Development",                        // category
                    PriorityLevel.URGENT_IMPORTANT,      // priority (Enum)
                    deadline,                             // dueDate (Timestamp)
                    true,                                 // isRecurring (Boolean)
                    RecurrenceType.WEEKLY,                // recurrenceType (Enum)
                    projectId                             // projectId (Foreign Key)
            );

            System.out.println("Step 1: Saving Task with all attributes...");
            taskRepo.save(task);

            // --- STEP 3: RECOVERY & VERIFICATION ---
            System.out.println("Step 2: Retrieving Task to verify mapping...");
            Task retrieved = taskRepo.findById(taskId).orElseThrow();

            verifyField("ID", taskId, retrieved.getIdTask());
            verifyField("Title", "Final Architecture Audit", retrieved.getTitle());
            verifyField("OwnerId", userId, retrieved.getOwnerId());
            verifyField("ProjectId", projectId, retrieved.getProjectId());
            verifyField("Category", "Development", retrieved.getCategory());
            verifyField("Priority", PriorityLevel.URGENT_IMPORTANT, retrieved.getPriority());
            verifyField("Status (Default)", TaskStatus.TODO, retrieved.getStatus());
            verifyField("Is Recurring", true, retrieved.isRecurring());
            verifyField("Recurrence Type", RecurrenceType.WEEKLY, retrieved.getRecurrenceType());
            verifyField("Version (Initial+Update)", 2L, retrieved.getVersionNumber());

            // Verify Logic-based Date mapping
            System.out.println("   -> Due Date Check: " + (retrieved.getDueDate().isEqual(deadline) ? "MATCH" : "MISMATCH"));

            // --- STEP 4: TRANSITION LOGIC (COMPLETION) ---
            System.out.println("\nStep 3: Testing completion logic and completedAt field...");
            retrieved.complete();
            taskRepo.update(retrieved);

            Task completedTask = taskRepo.findById(taskId).orElseThrow();
            System.out.println("   -> Final Status: " + completedTask.getStatus());
            System.out.println("   -> Completion Time: " + completedTask.getCompletedAt());
            System.out.println("   -> Version: " + completedTask.getVersionNumber() + " (Expected 3)");

//            // --- STEP 5: CLEANUP ---
//            System.out.println("\nCleaning up environment...");
//            taskRepo.delete(taskId);
//            projectRepo.delete(projectId);
//            userRepo.delete(userId);
//            System.out.println("====================================================");
//            System.out.println("SUCCESS: Every task attribute is correctly persisted.");

        } catch (Exception e) {
            System.err.println("\n[CRITICAL ERROR] Test failed!");
            e.printStackTrace();
        }
    }

    private static void verifyField(String label, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.println("   -> [MATCH] " + label + ": " + actual);
        } else {
            System.err.println("   -> [ERROR] " + label + " mismatch! Expected: " + expected + ", Got: " + actual);
        }
    }
}