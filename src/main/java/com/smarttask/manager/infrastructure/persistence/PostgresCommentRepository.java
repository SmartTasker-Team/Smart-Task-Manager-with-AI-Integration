package com.smarttask.manager.infrastructure.persistence;

import com.smarttask.manager.domain.model.Comment;
import com.smarttask.manager.domain.repository.CommentRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresCommentRepository implements CommentRepository {
    private final Connection conn;

    public PostgresCommentRepository(Connection conn) { this.conn = conn; }

    @Override
    public void save(Comment comment) {
        String sql = "INSERT INTO comments (id, task_id, author_id, content, version_number, created_at) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment.getIdComment());
            ps.setString(2, comment.getTaskId());
            ps.setString(3, comment.getUserId());
            ps.setString(4, comment.getContent());
            ps.setLong(5, comment.getVersionNumber());
            ps.setTimestamp(6, Timestamp.valueOf(comment.getCreatedAt()));
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean update(Comment comment) {
        String sql = "UPDATE comments SET content=?, version_number=? WHERE id=? AND version_number=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment.getContent());
            ps.setLong(2, comment.getVersionNumber());
            ps.setString(3, comment.getIdComment());
            ps.setLong(4, comment.getVersionNumber() - 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(String idComment) {
        // SCENARIO: DELETE CASCADE handles attachments automatically in Postgres
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM comments WHERE id=?")) {
            ps.setString(1, idComment);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Comment> findById(String idComment) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM comments WHERE id=?")) {
            ps.setString(1, idComment);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Comment c = new Comment(rs.getString("id"), rs.getString("content"),
                        rs.getString("task_id"), rs.getString("author_id"),
                        rs.getTimestamp("created_at").toLocalDateTime());
                c.loadVersion(rs.getLong("version_number"));
                return Optional.of(c);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Comment> findByTaskId(String taskId) { /* ... Select loop ... */ return new ArrayList<>(); }
}