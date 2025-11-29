package com.classmateai.backend.repository;

import com.classmateai.backend.entity.TranscriptionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranscriptionTagRepository extends JpaRepository<TranscriptionTag, TranscriptionTag.TranscriptionTagId> {

    @Query(value = "SELECT t.name FROM tag t " +
                   "JOIN transcription_tag tt ON t.id = tt.tag_id " +
                   "WHERE tt.transcription_id = :transcriptionId", 
           nativeQuery = true)
    List<String> findTagNamesByTranscriptionId(@Param("transcriptionId") Long transcriptionId);

    @Query(value = "SELECT COUNT(*) > 0 FROM transcription_tag " +
                   "WHERE transcription_id = :transcriptionId AND tag_id = :tagId", 
           nativeQuery = true)
    boolean existsByTranscriptionIdAndTagId(@Param("transcriptionId") Long transcriptionId, 
                                           @Param("tagId") Long tagId);

    @Modifying
    @Query(value = "INSERT INTO transcription_tag (transcription_id, tag_id) " +
                   "VALUES (:transcriptionId, :tagId)", 
           nativeQuery = true)
    void saveTranscriptionTag(@Param("transcriptionId") Long transcriptionId, 
                             @Param("tagId") Long tagId);
}