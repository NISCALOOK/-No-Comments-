package com.classmateai.backend.service;

import com.classmateai.backend.dto.QADto;
import com.classmateai.backend.dto.TaskResponse;
import com.classmateai.backend.dto.TranscriptionDetailResponse;
import com.classmateai.backend.dto.TranscriptionSimpleResponse;
import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.entity.User; 
import com.classmateai.backend.exception.ResourceNotFoundException;
import com.classmateai.backend.repository.TranscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranscriptionService {

    @Autowired
    private TranscriptionRepository transcriptionRepository;

    @Autowired
    private TaskService taskService; 

    private Long getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    // GET /api/transcriptions
    @Transactional(readOnly = true)
    public List<TranscriptionSimpleResponse> getTranscriptions() {
        Long userId = getCurrentUserId();
        
        List<Transcription> transcriptions = transcriptionRepository.findByUser_Id(userId);
        return transcriptions.stream().map(this::mapToSimpleResponse).collect(Collectors.toList());
    }

    // GET /api/transcriptions/{id}
    @Transactional(readOnly = true)
    public TranscriptionDetailResponse getTranscriptionDetails(Long transcriptionId) {
        Long userId = getCurrentUserId();

        // 1. Buscamos la transcripción ASEGURANDO que sea del usuario
        Transcription transcription = transcriptionRepository.findByIdAndUser_Id(transcriptionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transcripción no encontrada o no pertenece al usuario"));

        // 2. Mapeamos a la respuesta detallada
        return mapToDetailResponse(transcription);
    }


    // --- Funciones Mágicas (Mapeadores) ---
    // (Estos métodos permanecen intactos)

    private TranscriptionSimpleResponse mapToSimpleResponse(Transcription t) {
        TranscriptionSimpleResponse res = new TranscriptionSimpleResponse();
        res.setId(t.getId());
        res.setTitle(t.getTitle());
        res.setStatus(t.getStatus());
        res.setCreatedAt(t.getCreatedAt());
        return res;
    }

    private TranscriptionDetailResponse mapToDetailResponse(Transcription t) {
        TranscriptionDetailResponse res = new TranscriptionDetailResponse();
        res.setId(t.getId());
        res.setTitle(t.getTitle());
        res.setStatus(t.getStatus());
        res.setCreatedAt(t.getCreatedAt());
        res.setFullText(t.getFullText());
        res.setSummary(t.getSummary());

        List<TaskResponse> tasks = Collections.emptyList();
        res.setTasks(tasks);

        List<QADto> qas = Collections.emptyList();
        res.setQuestionsAndAnswers(qas);

        return res;
    }
}