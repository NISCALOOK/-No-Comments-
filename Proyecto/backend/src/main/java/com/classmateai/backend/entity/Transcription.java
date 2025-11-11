package com.classmateai.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // Importante

import java.time.LocalDateTime;
import java.util.Set; // Importante

@Entity
@Table(name = "transcription")
@Data
@NoArgsConstructor
public class Transcription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(columnDefinition = "TEXT")
    private String fullText;

    @Column
    private String audioFilePath;

    @Column
    private String status;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    // --- Relaciones ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- ¡AÑADIDO! (Relación inversa para Tareas) ---
    @OneToMany(mappedBy = "transcription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Task> tasks;
    
    // --- ¡AÑADIDO! (Relación inversa para Q&A) ---
    @OneToMany(mappedBy = "transcription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<QuestionAnswer> questionsAndAnswers;
}