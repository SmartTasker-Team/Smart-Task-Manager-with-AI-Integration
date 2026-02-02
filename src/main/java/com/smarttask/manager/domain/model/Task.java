package com.smarttask.manager.domain.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The primary Entity representing a unit of work.
 * <p>
 * Encapsulates state and behavior, including subtasks, dependencies, recurring schedules,
 * and priority levels. This entity ensures business invariants are maintained.
 * </p>
 */

public class Task {
    private String idTask;
    private String title;
    private String description;
    private String category;
    private PriorityLevel priority;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private boolean isRecurring;
    private String recurrenceRule;
    private String ownerId;
    private String parentTaskId;
    private long versionNumber;
    private List<Task> subtasks = new ArrayList<>();

    public Task(String idTask, String title, String ownerId) {
        this.idTask = idTask;
        this.title = title;
        this.ownerId = ownerId;
        this.priority = PriorityLevel.LOW;
        this.status = TaskStatus.TODO;
        this.versionNumber = 1;
    }

    public boolean isOverdue() {
        // Une tâche n'est pas en retard si elle est faite ou archivée
        if (status == TaskStatus.DONE || status == TaskStatus.ARCHIVED || dueDate == null) {
            return false;
        }
        return dueDate.isBefore(LocalDateTime.now());
    }

    public void addSubtask(Task subtask) {
        if (subtask.getIdTask().equals(this.idTask)) throw new IllegalArgumentException("Une tâche ne peut pas être sa propre sous-tâche");
        subtask.parentTaskId = this.idTask;
        this.subtasks.add(subtask);
        this.versionNumber++;
    }

    public void removeSubtask(Task subtask) {
        if (this.subtasks.remove(subtask)) {
            subtask.parentTaskId = null;
            this.versionNumber++;
        }
    }

    public void archive() {
        this.status = TaskStatus.ARCHIVED;
        this.versionNumber++;
    }


    private boolean hasActiveSubtasksRecursively(Task task) {
        for (Task subtask : task.subtasks) {
            // Si une sous-tâche n'est ni faite ni archivée
            if (subtask.getStatus() != TaskStatus.DONE && subtask.getStatus() != TaskStatus.ARCHIVED) {
                return true;
            }
            // Appel récursif pour vérifier les niveaux inférieurs
            if (hasActiveSubtasksRecursively(subtask)) {
                return true;
            }
        }
        return false;
    }
    public void complete() {
        if (hasActiveSubtasksRecursively(this)) {
            throw new IllegalStateException("Impossible de terminer : certaines sous-tâches (ou leurs propres sous-tâches) ne sont pas finies ou archivées.");
        }
        this.status = TaskStatus.DONE;
        this.versionNumber++;
    }


    public String getIdTask() { return idTask; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public PriorityLevel getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getDueDate() { return dueDate; }
    public boolean getIsRecurring() { return isRecurring; }
    public String getRecurrenceRule() { return recurrenceRule; }
    public String getOwnerId() { return ownerId; }
    public String getParentTaskId() { return parentTaskId; }
    public long getVersionNumber() { return versionNumber; }
    public List<Task> getSubtasks() { return new ArrayList<>(subtasks); }


    public void setTitle(String title){
        this.title = title;
        this.versionNumber++;
    }
    public void setDescription(String description){
        this.description = description;
        this.versionNumber++;
    }
    public void setCategory(String category){
        this.category = category;
        this.versionNumber++;
    }
    public void setPriority(PriorityLevel priority){
        this.priority = priority;
        this.versionNumber++;
    }
    public void setDueDate(LocalDateTime dueDate){
        this.dueDate = dueDate;
        this.versionNumber++;
    }
    public void setIsRecurring(boolean isRecurring){
        this.isRecurring = isRecurring;
        this.versionNumber++;
    }
    public void setRecurrenceRule(String recurrenceRule){
        this.recurrenceRule = recurrenceRule;
        this.versionNumber++;
    }
    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
        this.versionNumber++;
    }

/*@Override
    public String toString() {
        return "Task{" +
                "idTask='" + idTask + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", IsRecurring=" + isRecurring +
                ", recurrenceRule='" + recurrenceRule + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", parentTaskId='" + parentTaskId + '\'' +
                '}';
    }

    public static void main(String[] args){
        Task task1 = new Task("333333","hhhhhhhhh","3333333333");
        Task task2 = new Task("222222","hhhhhhhhh","3333333333");
        task1.addSubtask(task2);
        System.out.println(task1.getSubtasks());

        System.out.println(task1);
        task1.setCategory("Sport");
        System.out.println(task1);
        System.out.println(task1.getCategory());
    };
 */


}