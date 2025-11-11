package com.classmateai.backend.repository;

import com.classmateai.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // SEGURIDAD: Busca una tarea por su 'id' Y que además tenga el 'user_id' específico
    Optional<Task> findByIdAndUser_Id(Long taskId, Long userId);
    
    // FUNCIONALIDAD: Método necesario para listar tareas del usuario
    List<Task> findByUser_Id(Long userId); 
}