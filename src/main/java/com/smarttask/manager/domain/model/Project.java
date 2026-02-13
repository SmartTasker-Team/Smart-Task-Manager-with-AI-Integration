package com.smarttask.manager.domain.model;

import com.smarttask.manager.domain.exception.DomainException;
import java.util.Objects;

public class Project {
    private final String id;
    private final String ownerId;
    private String name;
    private String description;
    private String teamId;
    private long versionNumber;

    public Project(String id, String name, String ownerId, String teamId) {
        this.id = Objects.requireNonNull(id);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.teamId = teamId;
        this.versionNumber = 1;
        setName(name);
    }

    public void updateDetails(String name, String description, String teamId) {
        setName(name);
        this.description = description;
        this.teamId = teamId;
        this.versionNumber++;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Project name cannot be empty.");
        }
        this.name = name;
    }
    // --- INFRASTRUCTURE HOOKS ---
    // These allow the Repository to rebuild the object without triggering business logic
    public void setDescription(String description) { this.description = description; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public void loadVersion(long version) { this.versionNumber = version; }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerId() { return ownerId; }
    public String getTeamId() { return teamId; }
    public long getVersionNumber() { return versionNumber; }
}