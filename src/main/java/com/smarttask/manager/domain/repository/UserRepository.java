package com.smarttask.manager.domain.repository;

import java.util.Optional;
import com.smarttask.manager.domain.model.User;

/**
 * Interface defining the contract for User persistence and retrieval.
 * <p>
 * Provides methods for finding and managing user data required for local collaboration.
 * </p>
 */
public interface UserRepository {
    void save(User user);
    boolean update(User user); // Returns false if version conflict
    void delete(String idUser);
    Optional<User> findById(String idUser);
    Optional<User> findByEmail(String email);
}
