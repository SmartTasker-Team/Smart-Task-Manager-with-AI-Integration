package com.smarttask.manager.application.dto;

import java.time.Instant;

/**
 * Data Transfer Object representing a User in the local network.
 * <p>
 * Contains identification information required for automatic peer discovery and
 * collaboration features.
 * </p>
 */
public record UserDTO(
        String id,
        String email,
        String username
) {}
