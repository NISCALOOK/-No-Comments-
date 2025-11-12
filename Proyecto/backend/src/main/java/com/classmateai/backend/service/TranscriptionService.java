package com.classmateai.backend.service;

import com.classmateai.backend.dto.QADto;
import com.classmateai.backend.dto.TaskResponse;
import com.classmateai.backend.dto.TranscriptionDetailResponse;
import com.classmateai.backend.dto.TranscriptionSimpleResponse;
import com.classmateai.backend.entity.QuestionAnswer; // Importar la entidad
import com.classmateai.backend.entity.Task; // Importar la entidad
import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.exception.ResourceNotFoundException;
import com.classmateai.backend.repository.TranscriptionRepository;

// --- ¡AÑADE ESTOS IMPORTS! ---
import com.classmateai.backend.repository.QuestionAnswerRepository;
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

    // --- ¡AÑADE ESTOS REPOSITORIOS! ---
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;


    private Long getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    // GET /api/transcriptions (Sin cambios)
    @Transactional(readOnly = true)
    public List<TranscriptionSimpleResponse> getTranscriptions() {
        Long userId = getCurrentUserId();
        List<Transcription> transcriptions = transcriptionRepository.findByUser_Id(userId);
        return transcriptions.stream().map(this::mapToSimpleResponse).collect(Collectors.toList());
    }

    // --- ¡MÉTODO MODIFICADO! ---
    // GET /api/transcriptions/{id}
    @Transactional(readOnly = true)
    public TranscriptionDetailResponse getTranscriptionDetails(Long transcriptionId) {
        Long userId = getCurrentUserId();

        // 1. Buscamos la transcripción (Tu código ya era correcto)
        Transcription transcription = transcriptionRepository.findByIdAndUser_Id(transcriptionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transcripción no encontrada o no pertenece al usuario"));

        // --- ¡ESTA ES LA CORRECCIÓN! ---
        // 2. Buscamos EXPLÍCITAMENTE las tareas y Q&A usando los repositorios
        List<Task> tasks = taskRepository.findByTranscription_Id(transcriptionId);
        List<QuestionAnswer> qas = questionAnswerRepository.findByTranscription_Id(transcriptionId);

        // 3. Mapeamos todo junto
        return mapToDetailResponse(transcription, tasks, qas);
    }



    // --- Funciones Mágicas (Mapeadores) ---

    private TranscriptionSimpleResponse mapToSimpleResponse(Transcription t) {
        // (Este método no cambia)
        TranscriptionSimpleResponse res = new TranscriptionSimpleResponse();
        res.setId(t.getId());
        res.setTitle(t.getTitle());
        res.setStatus(t.getStatus());
        res.setCreatedAt(t.getCreatedAt());
        return res;
    }

    // --- ¡MÉTODO MODIFICADO! ---
    // Ahora acepta las listas que le pasamos
    private TranscriptionDetailResponse mapToDetailResponse(Transcription t, List<Task> tasks, List<QuestionAnswer> qas) {
        TranscriptionDetailResponse res = new TranscriptionDetailResponse();
        res.setId(t.getId());
        res.setTitle(t.getTitle());
        res.setStatus(t.getStatus());
        res.setCreatedAt(t.getCreatedAt());
        res.setFullText(t.getFullText());
        res.setSummary(t.getSummary());

        // Mapea las tareas (List<Task> -> List<TaskResponse>)
        List<TaskResponse> taskDtos = tasks.stream()
                .map(taskService::mapToTaskResponse)
                .collect(Collectors.toList());
        res.setTasks(taskDtos);

        // Mapea las Q&A (List<QuestionAnswer> -> List<QADto>)
        List<QADto> qaDtos = qas.stream()
                .map(this::mapToQADto)
                .collect(Collectors.toList());
        res.setQuestionsAndAnswers(qaDtos);

        return res;
    }
    
    // Función helper para mapear la entidad Q&A (Esta ya la tenías)
    private QADto mapToQADto(QuestionAnswer qa) {
        QADto dto = new QADto();
        dto.setQuestion(qa.getQuestion());
        dto.setAnswer(qa.getAnswer());
        return dto;
    }
}