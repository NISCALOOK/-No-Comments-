package com.classmateai.backend.repository;

import com.classmateai.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // (Tus métodos existentes están perfectos)
    Optional<Task> findByIdAndUser_Id(Long taskId, Long userId);
    List<Task> findByUser_Id(Long userId);

    // "Encuentra todas las Task que tengan este transcription_id"
    List<Task> findByTranscription_Id(Long transcriptionId);
}