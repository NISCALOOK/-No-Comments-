package com.classmateai.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatResponse {
    private String message;
    private LocalDateTime timestamp;
}