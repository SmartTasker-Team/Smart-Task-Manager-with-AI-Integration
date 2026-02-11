package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Team;
import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    void save(Team team);
    Optional<Team> findById(String id);
    List<Team> findAllByUserId(String userId); // Équipes dont l'utilisateur est membre
    void addMember(String teamId, String userId);
    void removeMember(String teamId, String userId);
}