package com.smarttask.manager.application.usecase.timelog;

import com.smarttask.manager.domain.model.TimeLog;
import com.smarttask.manager.domain.repository.TimeLogRepository;
import com.smarttask.manager.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.UUID;

public class TimeTrackingUseCase {
    private final TimeLogRepository repository;

    public TimeTrackingUseCase(TimeLogRepository repository) {
        this.repository = repository;
    }

    public String startTimer(String taskId) {
        // Rule: Only one active timer per task at a time
        if (repository.findActiveLogByTaskId(taskId).isPresent()) {
            throw new DomainException("A timer is already running for this task.");
        }

        TimeLog log = new TimeLog(UUID.randomUUID().toString(), taskId, LocalDateTime.now());
        repository.save(log);
        return log.getIdTimeLog();
    }

    public void stopTimer(String taskId) {
        TimeLog activeLog = repository.findActiveLogByTaskId(taskId)
                .orElseThrow(() -> new DomainException("No active timer found for this task."));

        activeLog.stopLog(LocalDateTime.now());

        if (!repository.update(activeLog)) {
            throw new DomainException("Conflict: Timer state was updated by another device.");
        }
    }
}