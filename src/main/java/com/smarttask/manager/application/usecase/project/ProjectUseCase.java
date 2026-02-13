package com.smarttask.manager.application.usecase.project;

import com.smarttask.manager.application.dto.ProjectDTO;
import com.smarttask.manager.domain.model.Project;
import com.smarttask.manager.domain.repository.ProjectRepository;
import com.smarttask.manager.domain.exception.DomainException;
import java.util.UUID;

public class ProjectUseCase {
    private final ProjectRepository repository;

    public ProjectUseCase(ProjectRepository repository) {
        this.repository = repository;
    }

    public String createProject(ProjectDTO dto) {
        Project project = new Project(
                UUID.randomUUID().toString(),
                dto.name(),
                dto.ownerId(),
                dto.teamId()
        );
        project.updateDetails(dto.name(), dto.description(), dto.teamId());
        repository.save(project);
        return project.getId();
    }

    public void updateProject(String id, ProjectDTO dto) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new DomainException("Project not found"));

        project.updateDetails(dto.name(), dto.description(), dto.teamId());

        if (!repository.update(project)) {
            throw new DomainException("CONFLICT: Project details were modified by another user.");
        }
    }
}