package com.smarttask.manager.infrastructure.persistence;

import com.smarttask.manager.domain.model.Team;
import com.smarttask.manager.domain.repository.TeamRepository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresTeamRepository implements TeamRepository {
    private final Connection conn;

    public PostgresTeamRepository(Connection conn) { this.conn = conn; }

    @Override
    public void save(Team team) {
        String sql = "INSERT INTO teams (id, name, owner_id, version_number, created_at) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, team.getId());
            ps.setString(2, team.getName());
            ps.setString(3, team.getOwnerId());
            ps.setLong(4, team.getVersionNumber());
            ps.setTimestamp(5, Timestamp.valueOf(team.getCreatedAt()));
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void addMemberToTeam(String teamId, String userId) {
        String sql = "INSERT INTO team_members (team_id, user_id) VALUES (?,?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teamId);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean update(Team team) {
        String sql = "UPDATE teams SET name=?, version_number=? WHERE id=? AND version_number=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, team.getName());
            ps.setLong(2, team.getVersionNumber());
            ps.setString(3, team.getId());
            ps.setLong(4, team.getVersionNumber() - 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Team> findById(String id) {
        String sql = "SELECT * FROM teams WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Team t = new Team(rs.getString("id"), rs.getString("name"),
                        rs.getString("owner_id"), rs.getTimestamp("created_at").toLocalDateTime());
                t.loadVersion(rs.getLong("version_number"));
                return Optional.of(t);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void removeMemberFromTeam(String teamId, String userId) { /* SQL Delete */ }


    @Override
    public List<Team> findTeamsByMember(String userId) {
        List<Team> teams = new ArrayList<>();
        // Jointure pour trouver les équipes où l'user est membre (ou owner)
        String sql = """
            SELECT t.* FROM teams t
            JOIN team_members tm ON t.id = tm.team_id
            WHERE tm.user_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user teams: " + e.getMessage());
        }
        return teams;
    }
    private Team mapResultSetToTeam(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime created = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();

        Team t = new Team(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("owner_id"),
                created
        );
        t.loadVersion(rs.getLong("version_number"));
        return t;
    }
}