package com.smarttask.manager.domain.model;

/**
 * Entity representing a team member in the system.
 * <p>
 * Identities users within the local network context to enable task sharing and
 * conflict resolution.
 * </p>
 */
public record User(Long id, String username, String email, String passwordHash) {}
