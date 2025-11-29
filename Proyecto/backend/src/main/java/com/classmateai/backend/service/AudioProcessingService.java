package com.classmateai.backend.service;

import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.repository.TranscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AudioProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(AudioProcessingService.class);

    @Autowired
    private WhisperService whisperService;

    @Autowired
    private LLMService llmService;

    @Autowired
    private TranscriptionRepository transcriptionRepository;

    @Autowired
    private TagService tagService;

    @Value("${storage.upload-dir}")
    private String uploadDir;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    @Async
    @Transactional
    public void processAudioAsync(File audioFile, Long transcriptionId, User user) {
        logger.info("Iniciando procesamiento asíncrono de audio para transcripción ID: {}", transcriptionId);
        
        Transcription transcription = transcriptionRepository.findById(transcriptionId)
            .orElseThrow(() -> new RuntimeException("Transcripción no encontrada: " + transcriptionId));

        Path tempFilePath = audioFile.toPath();
        try {
            // Paso 1: Transcribir audio con Whisper
            logger.info("Iniciando transcripción con Whisper...");
            transcription.setStatus("TRANSCRIBING");
            transcription.setUpdatedAt(LocalDateTime.now());
            transcriptionRepository.save(transcription);

            String fullText = whisperService.transcribeAudio(audioFile);
            
            if (fullText == null || fullText.trim().isEmpty()) {
                throw new RuntimeException("La transcripción no produjo texto");
            }

            // Guardar texto completo
            transcription.setFullText(fullText);
            transcription.setStatus("PROCESSING");
            transcription.setUpdatedAt(LocalDateTime.now());
            transcriptionRepository.save(transcription);

            logger.info("Transcripción completada. Texto长度: {} caracteres", fullText.length());

            // Paso 2: Generar resumen y título con LLM
            logger.info("Generando resumen y título con LLM...");
            String summaryResponse = llmService.generateSummaryAndTitle(fullText);
            
            // Parsear respuesta JSON - limpiar si viene con formato HTML
            String cleanResponse = summaryResponse.trim();
            if (cleanResponse.startsWith("<")) {
                // Si la respuesta empieza con <, probablemente es HTML, extraer solo el JSON
                int jsonStart = cleanResponse.indexOf("{");
                int jsonEnd = cleanResponse.lastIndexOf("}");
                if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                    cleanResponse = cleanResponse.substring(jsonStart, jsonEnd + 1);
                }
            }
            
            Map<String, String> summaryData = objectMapper.readValue(cleanResponse, Map.class);
            String title = summaryData.get("title");
            String summary = summaryData.get("summary");

            transcription.setTitle(title != null ? title : "Transcripción sin título");
            transcription.setSummary(summary != null ? summary : "Resumen no generado");
            transcription.setStatus("GENERATING_TAGS");
            transcription.setUpdatedAt(LocalDateTime.now());
            transcriptionRepository.save(transcription);

            logger.info("Resumen y título generados. Título: {}", title);

            // Paso 3: Generar etiquetas
            logger.info("Generando etiquetas...");
            List<String> tags = llmService.generateTags(fullText, summary);
            
            // Guardar etiquetas
            tagService.saveTagsForTranscription(transcription, tags);
            
            transcription.setStatus("COMPLETED");
            transcription.setUpdatedAt(LocalDateTime.now());
            transcriptionRepository.save(transcription);

            logger.info("Procesamiento completado exitosamente para transcripción ID: {}", transcriptionId);

        } catch (Exception e) {
            logger.error("Error en el procesamiento de audio para transcripción ID: {}", transcriptionId, e);
            
            transcription.setStatus("ERROR");
            transcription.setUpdatedAt(LocalDateTime.now());
            transcriptionRepository.save(transcription);
            
            // Podríamos guardar el error en un campo adicional si fuera necesario
            // transcription.setErrorMessage(e.getMessage());
        } finally {
            // Limpiar archivo temporal
            if (tempFilePath != null) {
                try {
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException e) {
                    // Log error but don't fail the transcription
                    System.err.println("Error al eliminar archivo temporal: " + e.getMessage());
                }
            }
        }
    }
}