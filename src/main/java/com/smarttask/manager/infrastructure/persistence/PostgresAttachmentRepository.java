package com.smarttask.manager.infrastructure.persistence;

import com.smarttask.manager.domain.model.Attachment;
import com.smarttask.manager.domain.repository.AttachmentRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresAttachmentRepository implements AttachmentRepository {
    private final Connection conn;

    public PostgresAttachmentRepository(Connection conn) { this.conn = conn; }

    @Override
    public void save(Attachment attachment) {
        String sql = "INSERT INTO attachments (id, comment_id, storage_url, file_type, version_number) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, attachment.getIdAttachment());
            ps.setString(2, attachment.getCommentId());
            ps.setString(3, attachment.getStorageUrl());
            ps.setString(4, attachment.getFileType());
            ps.setLong(5, attachment.getVersionNumber());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Attachment> findById(String id) {
        String sql = "SELECT * FROM attachments WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Attachment a = new Attachment(rs.getString("id"), rs.getString("comment_id"),
                        rs.getString("storage_url"), rs.getString("file_type"));
                a.loadVersion(rs.getLong("version_number"));
                return Optional.of(a);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Attachment> findByCommentId(String commentId) {
        List<Attachment> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM attachments WHERE comment_id = ?")) {
            ps.setString(1, commentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attachment a = new Attachment(rs.getString("id"), rs.getString("comment_id"),
                        rs.getString("storage_url"), rs.getString("file_type"));
                a.loadVersion(rs.getLong("version_number"));
                list.add(a);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public void delete(String idAttachment) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM attachments WHERE id=?")) {
            ps.setString(1, idAttachment);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}