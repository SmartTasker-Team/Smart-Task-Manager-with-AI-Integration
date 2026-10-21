package com.smarttask.manager;

import com.smarttask.manager.application.dto.UserDTO;
import com.smarttask.manager.application.usecase.user.UserUseCase;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresUserRepository;
import java.sql.Connection;

public class PostgresUserFullTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PostgresUserRepository repo = new PostgresUserRepository(conn);
            UserUseCase useCase = new UserUseCase(repo);

            System.out.println("--- Testing User Registration ---");
            UserDTO newUser = new UserDTO("JohnDoe", "john@example.com", "SecurePass123");
            String userId = useCase.register(newUser);
            System.out.println("User registered with ID: " + userId);

            System.out.println("\n--- Testing Duplicate Email Protection ---");
            try {
                useCase.register(newUser);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }

            System.out.println("\n--- Testing Deletion ---");
//            useCase.deleteAccount(userId);
//            System.out.println("Account deleted. Test Complete.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}