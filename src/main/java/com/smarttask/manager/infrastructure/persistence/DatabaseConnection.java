package com.smarttask.manager.infrastructure.persistence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Manages the connection to the Local PostgreSQL server.
 * <p>
 * Handles the configuration and lifecycle of the JDBC connection used by the repositories.
 * </p>
 */

public class DatabaseConnection {
    private static final String SQLITE_URL = "jdbc:sqlite:smarttask.db";
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/smarttask_db";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASS = "admin";

    private static Connection localConnection;
    private static Connection remoteConnection;

    public static Connection getLocalConnection() throws SQLException {
        if (localConnection == null || localConnection.isClosed()) {
            localConnection = DriverManager.getConnection(SQLITE_URL);
        }
        return localConnection;
    }

    public static Connection getRemoteConnection() throws SQLException {
        if (remoteConnection == null || remoteConnection.isClosed()) {
            remoteConnection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASS);
        }
        return remoteConnection;
    }

    public static void initialize() {
        try {
            executeScript(getLocalConnection(), "/sql/init_sqlite.sql");
            System.out.println("Local SQLite Database Initialized.");

            // Remote initialization is optional here depending on network status
            // executeScript(getRemoteConnection(), "/sql/init_postgres.sql");
        } catch (Exception e) {
            System.err.println("Database Initialization Failed: " + e.getMessage());
        }
    }

    private static void executeScript(Connection conn, String resourcePath) throws Exception {
        InputStream is = DatabaseConnection.class.getResourceAsStream(resourcePath);
        if (is == null) throw new RuntimeException("SQL script not found: " + resourcePath);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             Statement stmt = conn.createStatement()) {
            String sql = reader.lines().collect(Collectors.joining("\n"));
            for (String query : sql.split(";")) {
                if (!query.trim().isEmpty()) {
                    stmt.execute(query);
                }
            }
        }
    }
}