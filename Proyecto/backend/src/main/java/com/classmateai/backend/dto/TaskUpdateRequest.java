package com.classmateai.backend.dto;

import com.classmateai.backend.entity.TaskPriority;
import java.time.LocalDateTime;


public class TaskUpdateRequest {
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Boolean isCompleted;
    
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }
}
