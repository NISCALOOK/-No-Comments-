package com.classmateai.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TranscriptionDetailResponse {
    private Long id;
    private String title;
    private String status;
    private LocalDateTime createdAt;
    private String fullText;
    private String summary;
    private List<QADto> questionsAndAnswers;
    private List<TaskResponse> tasks;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<QADto> getQuestionsAndAnswers() { return questionsAndAnswers; }
    public void setQuestionsAndAnswers(List<QADto> questionsAndAnswers) { this.questionsAndAnswers = questionsAndAnswers; }
    public List<TaskResponse> getTasks() { return tasks; }
    public void setTasks(List<TaskResponse> tasks) { this.tasks = tasks; }
}