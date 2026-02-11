package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);
    Optional<Project> findById(String id);
    Optional<Project> findByName(String name);
    List<Project> findAllByTeam(String teamId);
    void delete(String id);
}