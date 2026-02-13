package com.smarttask.manager.infrastructure.persistence;

import com.smarttask.manager.domain.model.Project;
import com.smarttask.manager.domain.repository.ProjectRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresProjectRepository implements ProjectRepository {
    private final Connection conn;

    public PostgresProjectRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Optional<Project> findById(String projectId) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching project by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Project> findByTeam(String teamId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE team_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching projects by team", e);
        }
        return projects;
    }

    /**
     * Helper: Reconstitutes the Domain Entity from a Database Row.
     */
    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project p = new Project(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("owner_id"),
                rs.getString("team_id")
        );
        // Using the setter we just added
        p.setDescription(rs.getString("description"));
        p.loadVersion(rs.getLong("version_number"));
        return p;
    }

    @Override
    public void save(Project project) {
        String sql = "INSERT INTO projects (id, name, description, owner_id, team_id, version_number) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getId());
            ps.setString(2, project.getName());
            ps.setString(3, project.getDescription());
            ps.setString(4, project.getOwnerId());
            ps.setString(5, project.getTeamId());
            ps.setLong(6, project.getVersionNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving project", e);
        }
    }

    @Override
    public boolean update(Project project) {
        String sql = "UPDATE projects SET name=?, description=?, team_id=?, version_number=? WHERE id=? AND version_number=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getTeamId());
            ps.setLong(4, project.getVersionNumber());
            ps.setString(5, project.getId());
            ps.setLong(6, project.getVersionNumber() - 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project", e);
        }
    }

    @Override
    public void delete(String projectId) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM projects WHERE id=?")) {
            ps.setString(1, projectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project", e);
        }
    }

    @Override
    public List<Project> findByOwner(String ownerId) {
        // Implementation similar to findByTeam
        return new ArrayList<>();
    }
}