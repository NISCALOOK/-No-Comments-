package com.classmateai.backend.repository;

import com.classmateai.backend.entity.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {
    
    List<Transcription> findByUser_Id(Long userId);

    Optional<Transcription> findByIdAndUser_Id(Long transcriptionId, Long userId);
}
