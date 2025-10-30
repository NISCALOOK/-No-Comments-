package com.classmateai.backend.dto;

import java.time.LocalDateTime;

public class NoteResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}