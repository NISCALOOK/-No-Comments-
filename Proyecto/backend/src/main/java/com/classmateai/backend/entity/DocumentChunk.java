package com.classmateai.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_chunk")
public class DocumentChunk {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "transcription_id", nullable = false)
    private Long transcriptionId;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "embedding", nullable = false, columnDefinition = "VECTOR(2048)")
    private float[] embedding;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public DocumentChunk() {
        this.createdAt = LocalDateTime.now();
    }
    
    public DocumentChunk(Long transcriptionId, String content, float[] embedding) {
        this.transcriptionId = transcriptionId;
        this.content = content;
        this.embedding = embedding;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTranscriptionId() {
        return transcriptionId;
    }
    
    public void setTranscriptionId(Long transcriptionId) {
        this.transcriptionId = transcriptionId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public float[] getEmbedding() {
        return embedding;
    }
    
    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}