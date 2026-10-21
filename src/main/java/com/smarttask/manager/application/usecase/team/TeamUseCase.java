package com.smarttask.manager.application.usecase.team;

import com.smarttask.manager.domain.model.Team;
import com.smarttask.manager.domain.repository.TeamRepository;
import com.smarttask.manager.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TeamUseCase {
    private final TeamRepository repository;

    public TeamUseCase(TeamRepository repository) { this.repository = repository; }

    public String createTeam(String name, String ownerId) {
        Team team = new Team(UUID.randomUUID().toString(), name, ownerId, LocalDateTime.now());
        repository.save(team);
        // We also persist the owner as the first member in the join table
        repository.addMemberToTeam(team.getId(), ownerId);
        return team.getId();
    }

    public void addMember(String teamId, String userId) {
        Team team = repository.findById(teamId).orElseThrow(() -> new DomainException("Team not found"));
        team.addMember(userId);

        if (repository.update(team)) {
            repository.addMemberToTeam(teamId, userId);
        } else {
            throw new DomainException("Conflict: Team state updated by someone else.");
        }
    }

    public List<Team> getTeamsForUser(String userId) {
        return repository.findTeamsByMember(userId);
    }
}