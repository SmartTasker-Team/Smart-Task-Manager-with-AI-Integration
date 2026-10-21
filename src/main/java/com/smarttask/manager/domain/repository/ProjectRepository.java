package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);
    boolean update(Project project); // Returns false if version conflict
    void delete(String projectId);
    Optional<Project> findById(String projectId);
    List<Project> findByOwner(String ownerId);
    List<Project> findByTeam(String teamId);
}