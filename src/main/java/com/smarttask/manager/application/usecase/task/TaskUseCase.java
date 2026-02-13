package com.smarttask.manager.application.usecase.task;

import com.smarttask.manager.application.dto.TaskDTO;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.domain.repository.TaskRepository;
import com.smarttask.manager.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.UUID;

public class TaskUseCase {
    private final TaskRepository repository;

    public TaskUseCase(TaskRepository repository) { this.repository = repository; }

    public String create(TaskDTO dto) {
        Task task = new Task(UUID.randomUUID().toString(), dto.title(), dto.ownerId(), LocalDateTime.now());
        task.updateDetails(dto.title(), dto.description(), dto.category(),
                dto.priority(), dto.dueDate(), dto.isRecurring(),
                dto.recurrenceType(), dto.projectId());
        repository.save(task);
        return task.getIdTask();
    }

    public void update(String id, TaskDTO dto) {
        Task task = repository.findById(id).orElseThrow(() -> new DomainException("Task not found."));
        task.updateDetails(dto.title(), dto.description(), dto.category(),
                dto.priority(), dto.dueDate(), dto.isRecurring(),
                dto.recurrenceType(), dto.projectId());

        if (!repository.update(task)) {
            throw new DomainException("VERSION CONFLICT: This task was modified by another user. Please refresh.");
        }
    }

    public void delete(String id) {
        repository.delete(id);
    }
}