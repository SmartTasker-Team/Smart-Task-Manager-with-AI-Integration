package com.smarttask.manager.infrastructure.persistence;

import com.smarttask.manager.domain.model.TimeLog;
import com.smarttask.manager.domain.repository.TimeLogRepository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class PostgresTimeLogRepository implements TimeLogRepository {
    private final Connection conn;

    public PostgresTimeLogRepository(Connection conn) { this.conn = conn; }

    @Override
    public void save(TimeLog timeLog) {
        String sql = "INSERT INTO time_logs (id, task_id, start_time, version_number) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, timeLog.getIdTimeLog());
            ps.setString(2, timeLog.getTaskId());
            ps.setTimestamp(3, Timestamp.valueOf(timeLog.getStartTime()));
            ps.setLong(4, timeLog.getVersionNumber());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean update(TimeLog timeLog) {
        String sql = "UPDATE time_logs SET end_time=?, duration_seconds=?, version_number=? WHERE id=? AND version_number=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, timeLog.getEndTime() != null ? Timestamp.valueOf(timeLog.getEndTime()) : null);
            ps.setLong(2, timeLog.getDurationSeconds());
            ps.setLong(3, timeLog.getVersionNumber());
            ps.setString(4, timeLog.getIdTimeLog());
            ps.setLong(5, timeLog.getVersionNumber() - 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<TimeLog> findActiveLogByTaskId(String taskId) {
        String sql = "SELECT * FROM time_logs WHERE task_id = ? AND end_time IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    private TimeLog mapRow(ResultSet rs) throws SQLException {
        TimeLog log = new TimeLog(rs.getString("id"), rs.getString("task_id"), rs.getTimestamp("start_time").toLocalDateTime());
        Timestamp end = rs.getTimestamp("end_time");
        if (end != null) log.setEndTime(end.toLocalDateTime());
        log.setDurationSeconds(rs.getLong("duration_seconds"));
        log.loadVersion(rs.getLong("version_number"));
        return log;
    }

    @Override public Optional<TimeLog> findById(String id) { return Optional.empty(); }
    @Override public List<TimeLog> findByTaskId(String taskId) { return new ArrayList<>(); }
}