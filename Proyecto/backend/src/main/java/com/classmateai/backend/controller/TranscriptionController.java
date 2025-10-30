package com.classmateai.backend.controller;

import com.classmateai.backend.dto.TranscriptionDetailResponse;
import com.classmateai.backend.dto.TranscriptionSimpleResponse;
import com.classmateai.backend.service.TranscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transcriptions") // URL base: http://localhost:8080/api/transcriptions
public class TranscriptionController {

    @Autowired
    private TranscriptionService transcriptionService;

    // GET /api/transcriptions
    @GetMapping
    public ResponseEntity<List<TranscriptionSimpleResponse>> getTranscriptions() {
        return ResponseEntity.ok(transcriptionService.getTranscriptions());
    }

    // GET /api/transcriptions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TranscriptionDetailResponse> getTranscriptionDetails(@PathVariable Long id) {
        return ResponseEntity.ok(transcriptionService.getTranscriptionDetails(id));
    }
}