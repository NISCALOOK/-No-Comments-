package com.classmateai.backend.repository;

import com.classmateai.backend.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    
    // Este es el método que necesitamos:
    // "Encuentra todas las QuestionAnswer que tengan este transcription_id"
    List<QuestionAnswer> findByTranscription_Id(Long transcriptionId);
}