package com.classmateai.backend.repository;

import com.classmateai.backend.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    
    // Busca todas las notas que tengan un 'user_id' específico
    List<Note> findByUser_Id(Long userId);
    
    // Busca una nota por su 'id' Y que además tenga el 'user_id' específico
    // (Esto es por seguridad, para que no puedas borrar/editar notas de otro usuario)
    Optional<Note> findByIdAndUser_Id(Long noteId, Long userId);
}