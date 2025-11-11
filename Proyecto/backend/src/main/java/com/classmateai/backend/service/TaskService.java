package com.classmateai.backend.service;

import com.classmateai.backend.dto.TaskResponse;
import com.classmateai.backend.dto.TaskUpdateRequest;
import com.classmateai.backend.entity.Task;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.exception.ResourceNotFoundException;
import com.classmateai.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private Long getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
    
    // MÉTODO createTask ELIMINADO.
    
    // GET /api/tasks
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        Long userId = getCurrentUserId();
        
        List<Task> tasks = taskRepository.findByUser_Id(userId);
        return tasks.stream().map(this::mapToTaskResponse).collect(Collectors.toList());
    }
    
    // PUT /api/tasks/{id}
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Long userId = getCurrentUserId();

        // SEGURIDAD: Usa findByIdAndUser_Id para forzar el 404 si la tarea es ajena
        Task task = taskRepository.findByIdAndUser_Id(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada o no pertenece al usuario"));

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getIsCompleted() != null) {
            task.setCompleted(request.getIsCompleted());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    // DELETE /api/tasks/{id}
    @Transactional
    public void deleteTask(Long taskId) {
        Long userId = getCurrentUserId();

        Task task = taskRepository.findByIdAndUser_Id(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada o no pertenece al usuario"));

        taskRepository.delete(task);
    }

    public TaskResponse mapToTaskResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setDescription(task.getDescription());
        res.setPriority(task.getPriority());
        res.setDueDate(task.getDueDate());
        res.setCompleted(task.isCompleted());
        return res;
    }
}