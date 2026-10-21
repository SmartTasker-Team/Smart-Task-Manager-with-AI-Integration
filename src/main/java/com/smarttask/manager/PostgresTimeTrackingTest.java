package com.smarttask.manager;

import com.smarttask.manager.domain.model.*;
import com.smarttask.manager.application.usecase.timelog.TimeTrackingUseCase;
import com.smarttask.manager.infrastructure.persistence.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.UUID;

public class PostgresTimeTrackingTest {
    public static void main(String[] args) {
        System.out.println("=== Starting Time Tracking Integration Test ===");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Initialize Repositories
            PostgresUserRepository userRepo = new PostgresUserRepository(conn);
            PostgresTaskRepository taskRepo = new PostgresTaskRepository(conn);
            PostgresTimeLogRepository timeLogRepo = new PostgresTimeLogRepository(conn);

            // 2. Initialize Use Case
            TimeTrackingUseCase useCase = new TimeTrackingUseCase(timeLogRepo);

            // 3. Setup Dependencies (Requirement for Foreign Keys)
            String userId = UUID.randomUUID().toString();
            String taskId = UUID.randomUUID().toString();

            System.out.println("--- Step 1: Creating User and Task ---");
            userRepo.save(new User(userId, "TimeTester", "time@test.com", "HASHED_Secure123"));
            taskRepo.save(new Task(taskId, "Work on Architecture", userId, LocalDateTime.now()));

            // 4. Step 2: Start Timer
            System.out.println("--- Step 2: Starting Timer ---");
            String logId = useCase.startTimer(taskId);
            System.out.println("Timer started with ID: " + logId);

            // Simulating work
            System.out.println("Working... (2 seconds)");
            Thread.sleep(2000);

            // 5. Step 3: Stop Timer
            System.out.println("--- Step 3: Stopping Timer ---");
            useCase.stopTimer(taskId);

            // 6. Verification
            timeLogRepo.findActiveLogByTaskId(taskId).ifPresentOrElse(
                    l -> System.err.println("FAILED: Timer is still active!"),
                    () -> System.out.println("SUCCESS: Timer stopped and duration calculated.")
            );

//            // 7. Cleanup
//            taskRepo.delete(taskId);
//            userRepo.delete(userId);

        } catch (Exception e) {
            System.err.println("TEST FAILED!");
            e.printStackTrace();
        }
    }
}