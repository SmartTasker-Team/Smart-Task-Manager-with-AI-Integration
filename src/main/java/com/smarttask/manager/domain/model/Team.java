package com.smarttask.manager.domain.model;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Team {
    private String id;
    private String name;
    private String ownerId; // Créateur (pour info, mais sans droits spéciaux ici)
    private LocalDateTime createdAt;
    private List<String> memberIds; // Liste simple des IDs utilisateurs

    public Team(String id, String name, String ownerId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.memberIds = new ArrayList<>();
        this.memberIds.add(ownerId);
    }

    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            this.memberIds.add(userId);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getMemberIds() { return new ArrayList<>(memberIds); }

    public void setName(String name){ this.name = name ; }
}