package com.classmateai.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {
    private String id;
    private String summary;
    private String description;
    private String start;
    private String end;
    private String htmlLink;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}

