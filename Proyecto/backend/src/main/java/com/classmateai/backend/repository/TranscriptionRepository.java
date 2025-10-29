package com.classmateai.backend.repository;

import com.classmateai.backend.entity.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {
}
