package com.smarttask.manager.domain.service;

import java.time.LocalDateTime;

/**
 * Service to provide the current date and time.
 * Decouples the domain from the system clock for better testability.
 */
public interface DateTimeProvider {
    LocalDateTime now();
}