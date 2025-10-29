package com.classmateai.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
    private String fullText; // El texto completo de Whisper (necesario para tu IA_FLOW_GUIDE)

    @Column
    private String audioFilePath; // La ruta al archivo local (necesario para tu IA_FLOW_GUIDE)

    @Column
    private String status; // Ej: "PROCESSING", "TRANSCRIBED", "COMPLETED", "ERROR"

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    // --- Relación ---
    // Muchas transcripciones pertenecen a un usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Esta es la llave foránea
    private User user;
}
