package com.classmateai.backend.controller;

import com.classmateai.backend.dto.TaskResponse;
import com.classmateai.backend.dto.TaskUpdateRequest;
import com.classmateai.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    // GET /api/tasks (Listar Todas) 
    @GetMapping 
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // PUT /api/tasks/{id} (Actualizar)
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        TaskResponse updatedTask = taskService.updateTask(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    // DELETE /api/tasks/{id} (Eliminar)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build(); 
    }
}