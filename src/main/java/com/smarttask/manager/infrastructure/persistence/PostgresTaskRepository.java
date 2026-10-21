package com.smarttask.manager.infrastructure.persistence;

import com.smarttask.manager.domain.model.*;
import com.smarttask.manager.domain.repository.TaskRepository;
import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class PostgresTaskRepository implements TaskRepository {
    private final Connection conn;

    public PostgresTaskRepository(Connection conn) { this.conn = conn; }

    @Override
    public void save(Task task) {
        // Updated to 14 placeholders to include completed_at
        String sql = """
            INSERT INTO tasks (id, title, description, category, priority, status, 
            due_date, completed_at, is_recurring, recurrence_type, owner_id, 
            version_number, project_id, created_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            fillPreparedStatement(ps, task);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Save failed", e); }
    }

    @Override
    public boolean update(Task task) {
        // Added completed_at=? to the SET clause
        String sql = """
            UPDATE tasks SET title=?, description=?, category=?, priority=?, 
            status=?, due_date=?, completed_at=?, is_recurring=?, recurrence_type=?, 
            version_number=?, project_id=? 
            WHERE id=? AND version_number=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getCategory());
            ps.setString(4, task.getPriority().name());
            ps.setString(5, task.getStatus().name());
            ps.setTimestamp(6, task.getDueDate() != null ? Timestamp.valueOf(task.getDueDate()) : null);
            ps.setTimestamp(7, task.getCompletedAt() != null ? Timestamp.valueOf(task.getCompletedAt()) : null);
            ps.setBoolean(8, task.isRecurring());
            ps.setString(9, task.getRecurrenceType() != null ? task.getRecurrenceType().name() : null);
            ps.setLong(10, task.getVersionNumber()); // The new incremented version
            ps.setString(11, task.getProjectId());
            ps.setString(12, task.getIdTask());
            ps.setLong(13, task.getVersionNumber() - 1); // Optimistic Lock Check

            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Update failed", e); }
    }

    @Override
    public Optional<Task> findById(String idTask) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idTask);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapResultSetToTask(rs));
        } catch (SQLException e) { throw new RuntimeException("FindById failed", e); }
        return Optional.empty();
    }

    @Override
    public List<Task> findByOwner(String ownerId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE owner_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) tasks.add(mapResultSetToTask(rs));
        } catch (SQLException e) { throw new RuntimeException("FindByOwner failed", e); }
        return tasks;
    }

    @Override
    public void delete(String idTask) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tasks WHERE id=?")) {
            ps.setString(1, idTask);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Delete failed", e); }
    }

    // Helper: Map DB Row to Rich Domain Entity
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("owner_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        // Reconstitute state from DB
        task.loadVersion(rs.getLong("version_number"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));

        // Map the NEW field: completedAt
        Timestamp completedTs = rs.getTimestamp("completed_at");
        if (completedTs != null) {
            task.setCompletedAt(completedTs.toLocalDateTime());
        }

        task.updateDetails(
                rs.getString("title"), rs.getString("description"), rs.getString("category"),
                PriorityLevel.valueOf(rs.getString("priority")),
                TaskStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("due_date") != null ? rs.getTimestamp("due_date").toLocalDateTime() : null,
                rs.getTimestamp("completed_at") != null ? rs.getTimestamp("completed_at").toLocalDateTime() : null,
                rs.getBoolean("is_recurring"),
                rs.getString("recurrence_type") != null ? RecurrenceType.valueOf(rs.getString("recurrence_type")) : null,
                rs.getString("project_id")
        );

        // Reset version to match DB exactly after updateDetails incremented it
        task.loadVersion(rs.getLong("version_number"));
        return task;
    }

    private void fillPreparedStatement(PreparedStatement ps, Task task) throws SQLException {
        ps.setString(1, task.getIdTask());
        ps.setString(2, task.getTitle());
        ps.setString(3, task.getDescription());
        ps.setString(4, task.getCategory());
        ps.setString(5, task.getPriority().name());
        ps.setString(6, task.getStatus().name());
        ps.setTimestamp(7, task.getDueDate() != null ? Timestamp.valueOf(task.getDueDate()) : null);
        ps.setTimestamp(8, task.getCompletedAt() != null ? Timestamp.valueOf(task.getCompletedAt()) : null); // NEW
        ps.setBoolean(9, task.isRecurring());
        ps.setString(10, task.getRecurrenceType() != null ? task.getRecurrenceType().name() : null);
        ps.setString(11, task.getOwnerId());
        ps.setLong(12, task.getVersionNumber());
        ps.setString(13, task.getProjectId());
        ps.setTimestamp(14, Timestamp.valueOf(task.getCreatedAt()));
    }
}