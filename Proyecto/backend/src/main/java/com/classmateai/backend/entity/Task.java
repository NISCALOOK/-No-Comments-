package com.classmateai.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING) // Guarda el Enum como "alta", "media", "baja"
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.media;

    private LocalDateTime dueDate;

    @Column(nullable = false)
    private boolean isCompleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // --- Relación ---
    // Muchas tareas pertenecen a un usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
