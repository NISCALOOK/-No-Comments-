package com.classmateai.backend.service;

import com.classmateai.backend.dto.NoteRequest;
import com.classmateai.backend.dto.NoteResponse;
import com.classmateai.backend.entity.Note;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.exception.ResourceNotFoundException; 
import com.classmateai.backend.repository.NoteRepository;
import com.classmateai.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException; 

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    private Long getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    // GET /api/notes
    @Transactional(readOnly = true)
    public List<NoteResponse> getAllNotes() {
        Long userId = getCurrentUserId();
        
        List<Note> notes = noteRepository.findByUser_Id(userId);
        return notes.stream().map(this::mapToNoteResponse).collect(Collectors.toList());
    }

    // POST /api/notes
    @Transactional
    public NoteResponse createNote(NoteRequest request) {
        Long userId = getCurrentUserId();
        
        // 1. Buscamos al usuario dueño para asignarlo
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Error de seguridad: Usuario logueado no encontrado."));

        // 2. Creamos la nueva nota
        Note note = new Note();
        note.setUser(user);
        note.setContent(request.getContent());

        // 3. Guardamos en la BD
        Note savedNote = noteRepository.save(note);
        return mapToNoteResponse(savedNote);
    }

    // PUT /api/notes/{id}
    @Transactional
    public NoteResponse updateNote(Long noteId, NoteRequest request) {
        Long userId = getCurrentUserId();

        // 1. Buscamos la nota ASEGURANDO que sea del usuario logueado
        Note note = noteRepository.findByIdAndUser_Id(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada o no pertenece al usuario"));

        // 2. Actualizamos
        note.setContent(request.getContent());
        note.setUpdatedAt(LocalDateTime.now());
        
        Note updatedNote = noteRepository.save(note);
        return mapToNoteResponse(updatedNote);
    }

    // DELETE /api/notes/{id}
    @Transactional
    public void deleteNote(Long noteId) {
        Long userId = getCurrentUserId();

        // 1. Buscamos la nota por ID (Si no existe, lanza ResourceNotFoundException -> 404)
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada."));

        if (!note.getUser().getId().equals(userId)) {
            // Lanza AccessDeniedException para indicar explícitamente 403 Forbidden
            throw new AccessDeniedException("No tienes permiso para borrar esta nota.");
        }

        // 3. Borramos (si pasó la verificación)
        noteRepository.delete(note);
    }

    // Mapeador de Entidad a DTO
    private NoteResponse mapToNoteResponse(Note note) {
        NoteResponse res = new NoteResponse();
        res.setId(note.getId());
        res.setContent(note.getContent());
        res.setCreatedAt(note.getCreatedAt());
        return res;
    }
}