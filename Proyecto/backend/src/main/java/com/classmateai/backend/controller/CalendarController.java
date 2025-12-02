package com.classmateai.backend.controller;

import com.classmateai.backend.dto.CalendarEventDTO;
import com.classmateai.backend.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class CalendarController {

    @Autowired
    private GoogleCalendarService calendarService;

    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(@RequestParam String userId) {
        try {
            String authUrl = calendarService.getAuthorizationUrl(userId);
            return ResponseEntity.ok(Map.of("authUrl", authUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/authorize")
    public ResponseEntity<Map<String, String>> authorize(
            @RequestParam String code,
            @RequestParam String userId) {
        try {
            calendarService.authorize(code, userId);
            return ResponseEntity.ok(Map.of("message", "Successfully authorized"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/events")
    public ResponseEntity<?> getEvents(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int maxResults) {
        try {
            List<Event> events = calendarService.getUpcomingEvents(userId, maxResults);
            List<CalendarEventDTO> eventDTOs = events.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(eventDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(
            @RequestParam String userId,
            @RequestBody CalendarEventDTO eventDTO) {
        try {
            Event event = calendarService.createEvent(
                    userId,
                    eventDTO.getSummary(),
                    eventDTO.getDescription(),
                    eventDTO.getStartDateTime(),
                    eventDTO.getEndDateTime()
            );
            return ResponseEntity.ok(convertToDTO(event));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @RequestParam String userId,
            @PathVariable String eventId) {
        try {
            calendarService.deleteEvent(userId, eventId);
            return ResponseEntity.ok(Map.of("message", "Event deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private CalendarEventDTO convertToDTO(Event event) {
        CalendarEventDTO dto = new CalendarEventDTO();
        dto.setId(event.getId());
        dto.setSummary(event.getSummary());
        dto.setDescription(event.getDescription());
        dto.setHtmlLink(event.getHtmlLink());
        
        if (event.getStart() != null && event.getStart().getDateTime() != null) {
            dto.setStart(event.getStart().getDateTime().toString());
        }
        
        if (event.getEnd() != null && event.getEnd().getDateTime() != null) {
            dto.setEnd(event.getEnd().getDateTime().toString());
        }
        
        return dto;
    }
}

