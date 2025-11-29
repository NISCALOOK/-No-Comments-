package com.classmateai.backend.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private Long transcriptionId; // Opcional: para dar contexto de una transcripción específica
}