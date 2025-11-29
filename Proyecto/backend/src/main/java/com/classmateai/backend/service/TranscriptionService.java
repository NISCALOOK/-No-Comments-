package com.classmateai.backend.service;

import com.classmateai.backend.dto.TaskResponse;
import com.classmateai.backend.dto.TranscriptionDetailResponse;
import com.classmateai.backend.dto.TranscriptionSimpleResponse;
import com.classmateai.backend.entity.Task; // Importar la entidad
import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.exception.ResourceNotFoundException;
import com.classmateai.backend.repository.TranscriptionRepository;
import com.classmateai.backend.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranscriptionService {

    @Autowired
    private TranscriptionRepository transcriptionRepository;
    
    @Autowired
    private TaskService taskService; // Lo usamos para mapear

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TagService tagService;

    


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

        Transcription transcription = transcriptionRepository.findByIdAndUser_Id(transcriptionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transcripción no encontrada o no pertenece al usuario"));

        List<Task> tasks = taskRepository.findByTranscription_Id(transcriptionId);

        return mapToDetailResponse(transcription, tasks);
    }


    private TranscriptionSimpleResponse mapToSimpleResponse(Transcription t) {
        TranscriptionSimpleResponse res = new TranscriptionSimpleResponse();
        res.setId(t.getId());
        res.setTitle(t.getTitle());
        res.setStatus(t.getStatus());
        res.setCreatedAt(t.getCreatedAt());
        return res;
    }

    private TranscriptionDetailResponse mapToDetailResponse(Transcription t, List<Task> tasks) {
        TranscriptionDetailResponse res = new TranscriptionDetailResponse();
        res.setId(t.getId());
        res.setTitle(t.getTitle());
        res.setStatus(t.getStatus());
        res.setCreatedAt(t.getCreatedAt());
        res.setFullText(t.getFullText());
        res.setSummary(t.getSummary());

        List<TaskResponse> taskDtos = tasks.stream()
                .map(taskService::mapToTaskResponse)
                .collect(Collectors.toList());
        res.setTasks(taskDtos);
        
        // Obtener las etiquetas de la transcripción
        List<String> tags = tagService.getTagsForTranscription(t.getId());
        res.setTags(tags);

        return res;
    }
}
