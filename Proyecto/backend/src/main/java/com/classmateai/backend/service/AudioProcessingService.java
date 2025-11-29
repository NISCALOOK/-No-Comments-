package com.classmateai.backend.service;

import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.entity.Task;
import com.classmateai.backend.entity.DocumentChunk;
import com.classmateai.backend.repository.TranscriptionRepository;
import com.classmateai.backend.repository.DocumentChunkRepository;
import com.classmateai.backend.repository.TaskRepository;
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
import java.util.ArrayList;

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
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private TaskRepository taskRepository;

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
            
            Map<String, String> summaryData;
            try {
                summaryData = objectMapper.readValue(cleanResponse, Map.class);
            } catch (Exception e) {
                logger.warn("Error parseando JSON del LLM, usando respuesta como título: " + e.getMessage());
                // Si falla el parseo, usar la respuesta como título y resumen simple
                summaryData = Map.of(
                    "title", cleanResponse.length() > 60 ? cleanResponse.substring(0, 60) + "..." : cleanResponse,
                    "summary", cleanResponse
                );
            }
            
            String title = summaryData.get("title");
            String summary = summaryData.get("summary");

            transcription.setTitle(title != null ? title : "Transcripción sin título");
            transcription.setSummary(summary != null ? summary : "Resumen no generado");
            transcription.setStatus("GENERATING_TAGS");
            transcription.setUpdatedAt(LocalDateTime.now());
            transcriptionRepository.save(transcription);

            logger.info("Resumen y título generados. Título: {}", title);

            // Paso 3: Generar embeddings y chunks
            logger.info("Generando embeddings y chunks...");
            transcription.setStatus("GENERATING_EMBEDDINGS");
            transcription.setUpdatedAt(LocalDateTime.now());
            transcriptionRepository.save(transcription);

            generateDocumentChunks(transcription.getId(), fullText);

            // Paso 4: Generar etiquetas
            logger.info("Generando etiquetas...");
            List<String> tags = llmService.generateTags(fullText, summary);
            
            // Guardar etiquetas
            tagService.saveTagsForTranscription(transcription, tags);
            
            transcription.setStatus("COMPLETED");
            transcription.setUpdatedAt(LocalDateTime.now());
            transcriptionRepository.save(transcription);

            logger.info("Procesamiento completado exitosamente para transcripción ID: {}", transcriptionId);
            
            // Paso 5: Generar tareas automáticamente (asíncrono)
            logger.info("Iniciando generación automática de tareas...");
            generateTasksFromTranscription(transcriptionId, user.getId(), fullText, summary);

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

    @Async
    @Transactional
    public void generateTasksFromTranscription(Long transcriptionId, Long userId, String fullText, String summary) {
        try {
            logger.info("Generando tareas automáticas para transcripción ID: {}", transcriptionId);
            
            List<Task> tasks = llmService.extractTasksFromTranscription(fullText, summary, userId, transcriptionId);
            
            if (!tasks.isEmpty()) {
                taskRepository.saveAll(tasks);
                logger.info("Se generaron {} tareas automáticamente para transcripción ID: {}", tasks.size(), transcriptionId);
                
                // Log de las tareas generadas para debugging
                for (Task task : tasks) {
                    logger.info("Tarea generada: {} - Prioridad: {} - Fecha: {}", 
                        task.getDescription(), task.getPriority(), task.getDueDate());
                }
            } else {
                logger.info("No se encontraron tareas en la transcripción ID: {}", transcriptionId);
            }
            
        } catch (Exception e) {
            logger.error("Error generando tareas para transcripción ID: {}", transcriptionId, e);
            // No fallar el procesamiento principal si hay error en generación de tareas
        }
    }

    private void generateDocumentChunks(Long transcriptionId, String fullText) {
        try {
            logger.info("Dividiendo texto en chunks para transcripción ID: {}", transcriptionId);
            
            // Dividir el texto en chunks más pequeños (aproximadamente 1000 caracteres cada uno)
            List<String> chunks = splitTextIntoChunks(fullText, 1000);
            
            logger.info("Se generaron {} chunks para procesar", chunks.size());
            
            // Generar embeddings para cada chunk
            List<float[]> embeddings = embeddingService.generateBatchEmbeddings(chunks, "passage");
            
            // Guardar chunks con sus embeddings
            List<DocumentChunk> documentChunks = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                DocumentChunk chunk = new DocumentChunk(
                    transcriptionId, 
                    chunks.get(i), 
                    embeddings.get(i)
                );
                documentChunks.add(chunk);
            }
            
            // Guardar todos los chunks
            documentChunkRepository.saveAll(documentChunks);
            
            logger.info("Se guardaron {} chunks con embeddings para transcripción ID: {}", chunks.size(), transcriptionId);
            
        } catch (Exception e) {
            logger.error("Error generando chunks para transcripción ID: {}", transcriptionId, e);
            throw new RuntimeException("Error generando embeddings: " + e.getMessage(), e);
        }
    }

    private List<String> splitTextIntoChunks(String text, int maxChunkSize) {
        List<String> chunks = new ArrayList<>();
        
        // Dividir por párrafos primero (mejor regex para manejar diferentes saltos de línea)
        String[] paragraphs = text.split("\\n\\n+|\\r\\n\\r\\n+");
        
        StringBuilder currentChunk = new StringBuilder();
        
        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) continue;
            
            // Si el párrafo es más grande que el tamaño máximo, dividirlo
            if (paragraph.length() > maxChunkSize) {
                // Guardar el chunk actual si tiene contenido
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
                
                // Dividir párrafo largo por frases primero
                String[] sentences = paragraph.split("(?<=[.!?])\\s+");
                StringBuilder sentenceChunk = new StringBuilder();
                
                for (String sentence : sentences) {
                    sentence = sentence.trim();
                    if (sentence.isEmpty()) continue;
                    
                    // Si la frase es muy larga (más que maxChunkSize), dividirla por palabras
                    if (sentence.length() > maxChunkSize) {
                        // Guardar chunk actual de frases si existe
                        if (sentenceChunk.length() > 0) {
                            chunks.add(sentenceChunk.toString().trim());
                            sentenceChunk = new StringBuilder();
                        }
                        
                        // Dividir frase larga por palabras
                        String[] words = sentence.split("\\s+");
                        StringBuilder wordChunk = new StringBuilder();
                        
                        for (String word : words) {
                            word = word.trim();
                            if (word.isEmpty()) continue;
                            
                            if (wordChunk.length() + word.length() + 1 <= maxChunkSize) {
                                if (wordChunk.length() > 0) {
                                    wordChunk.append(" ");
                                }
                                wordChunk.append(word);
                            } else {
                                if (wordChunk.length() > 0) {
                                    chunks.add(wordChunk.toString().trim());
                                }
                                wordChunk = new StringBuilder(word);
                            }
                        }
                        
                        if (wordChunk.length() > 0) {
                            chunks.add(wordChunk.toString().trim());
                        }
                        
                    } else {
                        // Frase normal - verificar si cabe en el chunk actual
                        if (sentenceChunk.length() + sentence.length() + 1 <= maxChunkSize) {
                            if (sentenceChunk.length() > 0) {
                                sentenceChunk.append(" ");
                            }
                            sentenceChunk.append(sentence);
                        } else {
                            if (sentenceChunk.length() > 0) {
                                chunks.add(sentenceChunk.toString().trim());
                            }
                            sentenceChunk = new StringBuilder(sentence);
                        }
                    }
                }
                
                // Guardar el último chunk de frases si existe
                if (sentenceChunk.length() > 0) {
                    chunks.add(sentenceChunk.toString().trim());
                }
                
            } else {
                // Párrafo normal - verificar si cabe en el chunk actual
                if (currentChunk.length() + paragraph.length() + 2 <= maxChunkSize) {
                    if (currentChunk.length() > 0) {
                        currentChunk.append("\n\n");
                    }
                    currentChunk.append(paragraph);
                } else {
                    // Guardar el chunk actual y empezar uno nuevo
                    if (currentChunk.length() > 0) {
                        chunks.add(currentChunk.toString().trim());
                    }
                    currentChunk = new StringBuilder(paragraph);
                }
            }
        }
        
        // Guardar el último chunk si tiene contenido
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        // Validación final: asegurar que ningún chunk exceda el tamaño máximo
        List<String> validatedChunks = new ArrayList<>();
        for (String chunk : chunks) {
            if (chunk.length() <= maxChunkSize) {
                validatedChunks.add(chunk);
            } else {
                // Si todavía hay chunks muy largos, dividirlos forzosamente
                logger.warn("Chunk demasiado largo ({} caracteres), dividiendo forzosamente", chunk.length());
                for (int i = 0; i < chunk.length(); i += maxChunkSize) {
                    int end = Math.min(i + maxChunkSize, chunk.length());
                    validatedChunks.add(chunk.substring(i, end).trim());
                }
            }
        }
        
        // Logging para verificar tamaños
        logger.info("Chunks generados - Total: {}, Tamaños: {}", 
            validatedChunks.size(),
            validatedChunks.stream().map(String::length).toList());
        
        return validatedChunks;
    }
}