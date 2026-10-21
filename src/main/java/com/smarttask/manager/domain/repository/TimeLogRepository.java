package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.TimeLog;
import java.util.List;
import java.util.Optional;

public interface TimeLogRepository {
    void save(TimeLog timeLog);
    boolean update(TimeLog timeLog);
    Optional<TimeLog> findById(String id);
    List<TimeLog> findByTaskId(String taskId);
    /**
     * Finds the currently active (unstopped) timer for a task if it exists.
     */
    Optional<TimeLog> findActiveLogByTaskId(String taskId);
}