//package com.smarttask.manager.infrastructure.persistence;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseConnection {
//    // Update these with your remote Postgres credentials
//    private static final String URL = "jdbc:postgresql://your-remote-db:5433/smarttask";
//    private static final String USER = "postgres";
//    private static final String PASSWORD = "root";
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }
//}
package com.smarttask.manager.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // UPDATED: Use localhost and your actual database name/user
    private static final String URL = "jdbc:postgresql://localhost:5432/smarttask";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    public static Connection getConnection() throws SQLException {
        try {
            // Ensure the driver is loaded (standard for modern JDBC but good for testing)
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }
    }
}