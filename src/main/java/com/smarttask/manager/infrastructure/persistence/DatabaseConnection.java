package com.smarttask.manager.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection instance;

    private DatabaseConnection() {}

    public static synchronized Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                String url = "jdbc:postgresql://localhost:5432/smarttask";
                String user = "postgres";
                String password = "123";

                instance = DriverManager.getConnection(url, user, password);
                System.out.println("✅ New Database Connection Established.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return instance;
    }
}