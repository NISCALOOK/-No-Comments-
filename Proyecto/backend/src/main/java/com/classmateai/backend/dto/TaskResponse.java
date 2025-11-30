package com.classmateai.backend.dto;

import com.classmateai.backend.entity.TaskPriority;
import java.time.LocalDateTime;

public class TaskResponse {
    private Long id;
    private String description;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private boolean isCompleted;
    
<<<<<<< HEAD
=======
    // Getters y Setters
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454
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
<<<<<<< HEAD
}
=======
}
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454
