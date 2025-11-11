package com.classmateai.backend.dto;

import com.classmateai.backend.entity.TaskPriority;
import java.time.LocalDateTime;

// Usamos los tipos "Objeto" (Boolean, LocalDateTime)
// para que si el frontend envía 'null', signifique "no actualizar este campo".
public class TaskUpdateRequest {
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Boolean isCompleted;
    
    // Getters y Setters
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }
}