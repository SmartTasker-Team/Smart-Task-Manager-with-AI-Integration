package com.smarttask.manager.domain.repository;

import com.smarttask.manager.domain.model.TimeLog;
import java.util.List;
/**
 * Interface defining the contract for Time Tracking persistence.
 * <p>
 * This repository is central to the Productivity Insights engine. It records
 * duration segments spent on tasks, enabling the system to calculate performance
 * trends and Eisenhower-based priority adjustments.
 * </p>
 */

public interface TimeLogRepository {
    void save(TimeLog log);
    List<TimeLog> findByTaskId(String taskId);
    List<TimeLog> findByUserId(String userId);
    long getTotalDurationForTask(String taskId);
}