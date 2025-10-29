package com.classmateai.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user_account") 
@Data // Lombok: genera getters, setters, toString(), etc.
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // --- Relaciones ---
    // Un usuario puede tener muchas transcripciones
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transcription> transcriptions;

    // Un usuario puede tener muchas tareas
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Task> tasks;

    // Un usuario puede tener muchas notas
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Note> notes;
}
