package com.classmateai.backend.controller;

import com.classmateai.backend.dto.ChatRequest;
import com.classmateai.backend.dto.ChatResponse;
import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.repository.TranscriptionRepository;
import com.classmateai.backend.service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ChatController {

    @Autowired
    private LLMService llmService;

    @Autowired
    private TranscriptionRepository transcriptionRepository;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Obtener contexto si se proporcionó transcriptionId
            String context = "";
            if (request.getTranscriptionId() != null) {
                Transcription transcription = transcriptionRepository.findByIdAndUser_Id(
                    request.getTranscriptionId(), currentUser.getId())
                    .orElse(null);
                
                if (transcription != null) {
                    // Construir contexto con la transcripción
                    context = String.format("""
                        Título: %s
                        Resumen: %s
                        Texto completo: %s
                        """, 
                        transcription.getTitle(), 
                        transcription.getSummary() != null ? transcription.getSummary() : "No disponible",
                        transcription.getFullText() != null ? 
                            transcription.getFullText().substring(0, Math.min(2000, transcription.getFullText().length())) : 
                            "No disponible"
                    );
                }
            }

            // Llamar al servicio LLM
            String response = llmService.chatWithLLM(request.getMessage(), context);

            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setMessage(response);
            chatResponse.setTimestamp(java.time.LocalDateTime.now());

            return ResponseEntity.ok(chatResponse);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}