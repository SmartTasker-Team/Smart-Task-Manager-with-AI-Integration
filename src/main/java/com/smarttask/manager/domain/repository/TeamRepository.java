package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    void save(Team team);
    boolean update(Team team);
    void addMemberToTeam(String teamId, String userId);
    void removeMemberFromTeam(String teamId, String userId);
    Optional<Team> findById(String id);
    List<Team> findTeamsByMember(String userId);
}