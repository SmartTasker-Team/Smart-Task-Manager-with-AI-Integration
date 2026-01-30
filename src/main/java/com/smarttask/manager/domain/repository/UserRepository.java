package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.User;

import java.util.Optional;

/**
 * Interface defining the contract for User persistence and retrieval.
 * <p>
 * Provides methods for finding and managing user data required for local collaboration.
 * </p>
 */
public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}
