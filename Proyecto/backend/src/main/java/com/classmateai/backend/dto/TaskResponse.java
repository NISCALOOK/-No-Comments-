package com.classmateai.backend.dto;

import com.classmateai.backend.entity.TaskPriority;
import java.time.LocalDateTime;

public class TaskResponse {
    private Long id;
    private String description;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private boolean isCompleted;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
