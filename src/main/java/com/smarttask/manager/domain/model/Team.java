package com.smarttask.manager.domain.model;

import com.smarttask.manager.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Team {
    private final String id;
    private final String ownerId;
    private final LocalDateTime createdAt;
    private String name;
    private long versionNumber;
    private final List<String> memberIds;

    public Team(String id, String name, String ownerId, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.createdAt = createdAt;
        this.memberIds = new ArrayList<>();
        this.memberIds.add(ownerId); // Owner is the first member
        this.versionNumber = 1;
        setName(name);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Team name cannot be empty.");
        }
        this.name = name;
    }

    public void addMember(String userId) {
        if (memberIds.contains(userId)) {
            throw new DomainException("User is already a member of this team.");
        }
        this.memberIds.add(userId);
        this.versionNumber++;
    }

    public void loadVersion(long version) { this.versionNumber = version; }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwnerId() { return ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<String> getMemberIds() { return Collections.unmodifiableList(memberIds); }
    public long getVersionNumber() { return versionNumber; }
}