package com.classmateai.backend.dto;

import com.classmateai.backend.entity.TaskPriority;
import java.time.LocalDateTime;

<<<<<<< HEAD

=======
// Usamos los tipos "Objeto" (Boolean, LocalDateTime)
// para que si el frontend envía 'null', signifique "no actualizar este campo".
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454
public class TaskUpdateRequest {
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Boolean isCompleted;
    
<<<<<<< HEAD
=======
    // Getters y Setters
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }
<<<<<<< HEAD
}
=======
}
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454
