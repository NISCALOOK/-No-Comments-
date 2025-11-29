package com.classmateai.backend.repository;

import com.classmateai.backend.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {
    
    List<DocumentChunk> findByTranscriptionId(Long transcriptionId);
    
    @Query(value = """
        SELECT * FROM document_chunk dc
        WHERE dc.transcription_id IN (
            SELECT t.id FROM transcription t 
            WHERE t.user_id = :userId
        )
        ORDER BY dc.embedding <=> :queryEmbedding::vector
        LIMIT :limit
        """, nativeQuery = true)
    List<DocumentChunk> findNearestChunksByUser(@Param("userId") Long userId, 
                                               @Param("queryEmbedding") float[] queryEmbedding, 
                                               @Param("limit") int limit);
    
    @Query(value = """
        SELECT * FROM document_chunk dc
        WHERE dc.transcription_id = :transcriptionId
        ORDER BY dc.embedding <=> :queryEmbedding::vector
        LIMIT :limit
        """, nativeQuery = true)
    List<DocumentChunk> findNearestChunksByTranscription(@Param("transcriptionId") Long transcriptionId, 
                                                         @Param("queryEmbedding") float[] queryEmbedding, 
                                                         @Param("limit") int limit);
    
    void deleteByTranscriptionId(Long transcriptionId);
}