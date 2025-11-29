package com.classmateai.backend.controller;

import com.classmateai.backend.dto.TranscriptionSimpleResponse;
import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.repository.TranscriptionRepository;
import com.classmateai.backend.service.AudioProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/audio")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AudioController {

    @Autowired
    private AudioProcessingService audioProcessingService;

    @Autowired
    private TranscriptionRepository transcriptionRepository;

    @PostMapping("/upload")
    public ResponseEntity<TranscriptionSimpleResponse> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            System.out.println("Archivo recibido: " + file.getOriginalFilename());
            System.out.println("Tamaño: " + file.getSize() + " bytes");
            System.out.println("Content-Type: " + file.getContentType());

            // Validar tipo de archivo (audio) - temporalmente desactivado para pruebas
            String contentType = file.getContentType();
            // if (contentType == null || !contentType.startsWith("audio/")) {
            //     return ResponseEntity.badRequest().build();
            // }

            // Obtener usuario actual
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Crear transcripción inicial
            Transcription transcription = new Transcription();
            transcription.setTitle("Procesando audio...");
            transcription.setStatus("PROCESSING");
            transcription.setUser(currentUser);
            transcription.setCreatedAt(LocalDateTime.now());
            transcription.setUpdatedAt(LocalDateTime.now());

            // Guardar ruta temporal del archivo
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            transcription.setAudioFilePath(fileName);

            transcription = transcriptionRepository.save(transcription);

            // Guardar archivo temporalmente ANTES del procesamiento asíncrono
            Path uploadPath = Path.of("./classmate-uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".wav";
            String tempFileName = UUID.randomUUID().toString() + extension;
            Path tempFilePath = uploadPath.resolve(tempFileName);
            
            // Copiar el archivo inmediatamente 
            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Iniciar procesamiento asíncrono con archivo guardado
            audioProcessingService.processAudioAsync(tempFilePath.toFile(), transcription.getId(), currentUser);

            // Devolver respuesta inmediata
            TranscriptionSimpleResponse response = new TranscriptionSimpleResponse();
            response.setId(transcription.getId());
            response.setTitle(transcription.getTitle());
            response.setStatus(transcription.getStatus());
            response.setCreatedAt(transcription.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status/{transcriptionId}")
    public ResponseEntity<TranscriptionSimpleResponse> getProcessingStatus(@PathVariable Long transcriptionId) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            Transcription transcription = transcriptionRepository.findByIdAndUser_Id(transcriptionId, currentUser.getId())
                .orElse(null);

            if (transcription == null) {
                return ResponseEntity.notFound().build();
            }

            TranscriptionSimpleResponse response = new TranscriptionSimpleResponse();
            response.setId(transcription.getId());
            response.setTitle(transcription.getTitle());
            response.setStatus(transcription.getStatus());
            response.setCreatedAt(transcription.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
