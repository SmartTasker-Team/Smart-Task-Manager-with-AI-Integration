package com.smarttask.manager.infrastructure.persistence;

import com.smarttask.manager.domain.model.User;
import com.smarttask.manager.domain.repository.UserRepository;
import java.sql.*;
import java.util.Optional;

public class PostgresUserRepository implements UserRepository {
    private final Connection conn;

    public PostgresUserRepository(Connection conn) { this.conn = conn; }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (id, username, email, password_hash, version_number, created_at) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getIdUser());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setLong(5, user.getVersionNumber());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Error saving user", e); }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username=?, email=?, password_hash=?, version_number=? WHERE id=? AND version_number=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setLong(4, user.getVersionNumber()); // Already incremented
            ps.setString(5, user.getIdUser());
            ps.setLong(6, user.getVersionNumber() - 1); // Optimistic Lock check

            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Error updating user", e); }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void delete(String idUser) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setString(1, idUser);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<User> findById(String idUser) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setString(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User(rs.getString("id"), rs.getString("username"), rs.getString("email"), rs.getString("password_hash"));
        u.loadVersion(rs.getLong("version_number"));
        return u;
    }
}