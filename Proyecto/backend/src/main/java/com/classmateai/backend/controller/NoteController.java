package com.classmateai.backend.controller;

import com.classmateai.backend.dto.NoteRequest;
import com.classmateai.backend.dto.NoteResponse;
import com.classmateai.backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/notes") // Define la URL base (http://localhost:8080/api/notes)
public class NoteController {

    @Autowired 
    private NoteService noteService;

    // GET /api/notes
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes()); 
    }

    // POST /api/notes
    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@RequestBody NoteRequest request) {
        NoteResponse createdNote = noteService.createNote(request);
        return new ResponseEntity<>(createdNote, HttpStatus.CREATED); 
    }

    // PUT /api/notes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long id, @RequestBody NoteRequest request) {
        NoteResponse updatedNote = noteService.updateNote(id, request);
        return ResponseEntity.ok(updatedNote); 
    }

    // DELETE /api/notes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build(); 
    }
}