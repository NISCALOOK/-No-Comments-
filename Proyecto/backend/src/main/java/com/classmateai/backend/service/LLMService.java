package com.classmateai.backend.service;

import com.classmateai.backend.entity.DocumentChunk;
import com.classmateai.backend.entity.Task;
import com.classmateai.backend.entity.TaskPriority;
import com.classmateai.backend.entity.User;
import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.repository.DocumentChunkRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LLMService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${llm.api.url}")
    private String llmApiUrl;

    @Value("${llm.api.key}")
    private String llmApiKey;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private EmbeddingService embeddingService;

    public LLMService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String generateSummaryAndTitle(String fullText) {
        String prompt = buildSummaryPrompt(fullText);
        
        Map<String, Object> request = Map.of(
            "model", "minimax-m2-free",
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.3,
            "max_tokens", 10000
        );

        try {
            String response = webClient.post()
                .uri(llmApiUrl + "/chat/completions")
                .header("Authorization", "Bearer " + llmApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return parseLLMResponse(response);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar resumen y título con LLM: " + e.getMessage(), e);
        }
    }

    public List<String> generateTags(String fullText, String summary) {
        String prompt = buildTagsPrompt(fullText, summary);
        
        Map<String, Object> request = Map.of(
            "model", "minimax-m2-free",
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.2,
            "max_tokens", 5000
        );

        try {
            String response = webClient.post()
                .uri(llmApiUrl + "/chat/completions")
                .header("Authorization", "Bearer " + llmApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return parseTagsResponse(response);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar etiquetas con LLM: " + e.getMessage(), e);
        }
    }

    public String chatWithLLM(String message, String context, Long userId, Long transcriptionId) {
        try {
            String ragContext = "";
            try {
                // Generar embedding para la pregunta del usuario
                float[] queryEmbedding = embeddingService.generateQueryEmbedding(message);
                
                if (transcriptionId != null) {
                    List<DocumentChunk> relevantChunks = documentChunkRepository
                        .findNearestChunksByTranscription(transcriptionId, queryEmbedding, 5);
                    ragContext = buildRAGContext(relevantChunks);
                } else {
                    List<DocumentChunk> relevantChunks = documentChunkRepository
                        .findNearestChunksByUser(userId, queryEmbedding, 5);
                    ragContext = buildRAGContext(relevantChunks);
                }
            } catch (Exception ragError) {
                ragContext = "";
            }
            

            String fullContext = combineContexts(context, ragContext);
            
            String prompt = buildChatPrompt(message, fullContext);
            
            Map<String, Object> request = Map.of(
                "model", "minimax-m2-free",
                "messages", List.of(
                    Map.of("role", "system", "content", "Eres un asistente académico especializado en ayudar a estudiantes con sus clases y apuntes. Usa el contexto proporcionado para dar respuestas precisas y relevantes."),
                    Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.5,
                "max_tokens", 10000
            );

            String response = webClient.post()
                .uri(llmApiUrl + "/chat/completions")
                .header("Authorization", "Bearer " + llmApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return parseChatResponse(response);
            
        } catch (Exception e) {
            return "Lo siento, tuve un problema al procesar tu pregunta. Por favor, intenta nuevamente. Error: " + e.getMessage();
        }
    }

    private String buildRAGContext(List<DocumentChunk> chunks) {
        if (chunks.isEmpty()) {
            return "";
        }
        
        return chunks.stream()
            .map(chunk -> chunk.getContent())
            .collect(Collectors.joining("\n\n---\n\n"));
    }

    private String combineContexts(String originalContext, String ragContext) {
        if (ragContext.isEmpty()) {
            return originalContext;
        }
        
        if (originalContext == null || originalContext.isEmpty()) {
            return "Contexto relevante de tus apuntes:\n\n" + ragContext;
        }
        
        return originalContext + "\n\nContexto adicional relevante de tus apuntes:\n\n" + ragContext;
    }

    private String buildSummaryPrompt(String fullText) {
        return String.format("""
            Analiza el siguiente texto de una clase transcrita y genera un título y resumen REALES basados en el contenido.
            
            INSTRUCCIONES IMPORTANTES:
            - NO uses placeholders como "título aquí" o "resumen aquí"
            - Genera contenido ORIGINAL basado en el texto proporcionado
            - El título debe ser específico al contenido (máximo 60 caracteres)
            - El resumen debe extraer los puntos principales del texto
            
            Responde ÚNICAMENTE en formato JSON con contenido real:
            {
              "title": "título real basado en el contenido",
              "summary": "resumen real basado en el contenido"
            }
            
            Texto a analizar:
            %s
            """, fullText);
    }

    private String buildTagsPrompt(String fullText, String summary) {
        String contentToAnalyze;

        if (summary != null && !summary.isBlank()) {
            contentToAnalyze = summary;
        } else {
            contentToAnalyze = fullText;
        }

        return String.format("""
            Tu tarea es generar 5 etiquetas conceptuales que abarquen el contenido que se te da.

            Texto: %s

            INSTRUCCIONES OBLIGATORIAS:
            1. NO escribas introducciones ni pensamientos.
            2. Tu respuesta FINAL debe empezar OBLIGATORIAMENTE con la palabra clave: "###LISTA###".
            3. Inmediatamente después, pon las etiquetas separadas por "||".

            Ejemplo EXACTO de respuesta requerida:
            ###LISTA###Matemáticas || Examen || Universidad || Cálculo || Martes
            """, contentToAnalyze);
    }

    private String buildChatPrompt(String message, String context) {
        return String.format("""
            Contexto de la clase: %s
            
            Pregunta del estudiante: %s
            
            Responde de manera clara, concisa y útil para el estudiante.
            """, context, message);
    }

    private String parseLLMResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    String content = message.get("content").asText();


                    int endOfThink = content.lastIndexOf("</think>");
                    if (endOfThink != -1) {

                        content = content.substring(endOfThink + 8);
                    }
                    content = content.trim();
                    // --------------------------------

                    try {

                        if (content.startsWith("```")) {
                            content = content.replace("```json", "").replace("```", "").trim();
                        }

                        JsonNode jsonContent = objectMapper.readTree(content);
                        String title = jsonContent.get("title").asText();
                        String summary = jsonContent.get("summary").asText();


                        if (title.contains("aquí") || title.contains("titulo") || title.length() < 5) {
                            title = "Transcripción generada";
                        }
                        if (summary.contains("aquí") || summary.contains("resumen") || summary.length() < 10) {
                            summary = "Resumen generado automáticamente del contenido de la clase.";
                        }

                        return String.format("{\"title\":\"%s\",\"summary\":\"%s\"}",
                            title.replace("\"", "\\\""), summary.replace("\"", "\\\""));
                    } catch (Exception e) {

                        if (content.length() > 60) {
                            String title = content.substring(0, 60).replaceAll("[^\\w\\sáéíóúÁÉÍÓÚñÑ-]", "").trim() + "...";
                            String summary = content.replaceAll("[^\\w\\sáéíóúÁÉÍÓÚñÑ.(),-]", "").trim();
                            return String.format("{\"title\":\"%s\",\"summary\":\"%s\"}",
                                title.replace("\"", "\\\""), summary.replace("\"", "\\\""));
                        }
                        return String.format("{\"title\":\"Transcripción procesada\",\"summary\":\"%s\"}",
                            content.replace("\"", "\\\""));
                    }
                }
            }
            throw new RuntimeException("Respuesta inválida del LLM");
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear respuesta del LLM: " + e.getMessage(), e);
        }
    }
    private List<String> parseTagsResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    String content = message.get("content").asText();

                    int endOfThink = content.lastIndexOf("</think>");
                    if (endOfThink != -1) {
                        content = content.substring(endOfThink + 8);
                    }

                    content = content.replace("###LISTA###", "")
                                     .replace("```json", "")
                                     .replace("```", "")
                                     .trim();
                    String[] rawTags = content.split("[,|\\n]+");

                    List<String> tags = new ArrayList<>();
                    for (String rawTag : rawTags) {
                        String cleanTag = rawTag.trim()
                                                .replaceAll("^[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]+", "") // Borra "1.", "-", "*" al inicio
                                                .replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s-]", ""); // Borra emojis o símbolos raros
                        if (cleanTag.length() > 2 &&
                            !cleanTag.equalsIgnoreCase("tags") &&
                            cleanTag.length() < 30) {

                            tags.add(cleanTag);
                        }
                    }

                    // Devolver máximo 6 etiquetas únicas
                    return tags.stream().distinct().limit(6).collect(Collectors.toList());
                }
            }
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<String> parseTagsFromCSV(String content) {
        List<String> tags = new ArrayList<>();
        content = content.replace("{", "").replace("}", "").replace("\"tags\":", "").replace("[", "").replace("]", "");
        String[] tagArray = content.split(",");
        for (String tag : tagArray) {
            String cleanTag = tag.trim().replaceAll("[^\\w\\sáéíóúÁÉÍÓÚñÑ-]", "");
            if (!cleanTag.isEmpty() && cleanTag.length() > 1 && !cleanTag.startsWith("think")) {
                tags.add(cleanTag);
            }
        }
        return tags;
    }


    private String parseChatResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    return message.get("content").asText();
                }
            }
            throw new RuntimeException("Respuesta inválida del LLM para chat");
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear respuesta del chat: " + e.getMessage(), e);
        }
    }

    public List<Task> extractTasksFromTranscription(String transcriptionText, String summary, Long userId, Long transcriptionId) {
        try {
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String currentDayOfWeek = LocalDate.now().getDayOfWeek().toString().toLowerCase();
            
            String prompt = String.format("""
                Analiza el siguiente texto de una clase transcrita y extrae todas las tareas, parciales, recordatorios o actividades mencionadas.
                
                Fecha actual: %s (%s)
                
                Instrucciones:
                1. Busca menciones de tareas, trabajos, parciales, recordatorios, fechas de entrega, etc.
                2. Para cada tarea encontrada, determina:
                   - Descripción clara y concisa
                   - Prioridad (alta/media/baja) según urgencia e importancia
                   - Fecha de entrega si se menciona (ej: "para el martes", "para el viernes", "para la próxima semana")
                   - Hora si se menciona (ej: "a las 7 pm", "a las 6 pm", "a las 10 am")
                   - Si no hay fecha específica, no asignes fecha
                   - Si no hay hora específica, no asignes hora (usa null)
                3. Responde SOLO en formato JSON con este exacto formato:
                   {
                     "tasks": [
                       {
                         "description": "descripción de la tarea",
                         "priority": "alta|media|baja", 
                         "due_date": "YYYY-MM-DD" o null,
                         "due_time": "HH:MM" o null (formato 24h, ej: "19:00" para 7pm, "18:00" para 6pm)
                       }
                     ]
                   }
                4. Si no hay tareas mencionadas, responde con {"tasks": []}
                
                Texto a analizar:
                %s
                
                Resumen:
                %s
                """, currentDate, currentDayOfWeek, transcriptionText, summary);
            
            Map<String, Object> request = Map.of(
                "model", "minimax-m2-free",
                "messages", List.of(
                    Map.of("role", "system", "content", "Eres un asistente académico experto en identificar tareas y fechas de entrega en transcripciones de clases."),
                    Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.2,
                "max_tokens", 5000
            );

            String response = webClient.post()
                .uri(llmApiUrl + "/chat/completions")
                .header("Authorization", "Bearer " + llmApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return parseTasksResponse(response, userId, transcriptionId);
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<Task> parseTasksResponse(String response, Long userId, Long transcriptionId) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    String content = message.get("content").asText();
                    
                    String cleanContent = content.trim();
                    if (cleanContent.startsWith("<")) {
                        int jsonStart = cleanContent.indexOf("{");
                        int jsonEnd = cleanContent.lastIndexOf("}");
                        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                            cleanContent = cleanContent.substring(jsonStart, jsonEnd + 1);
                        }
                    }
                    
                    Map<String, Object> tasksData = objectMapper.readValue(cleanContent, Map.class);
                    List<Map<String, Object>> tasksList = (List<Map<String, Object>>) tasksData.get("tasks");
                    
                    List<Task> tasks = new ArrayList<>();
                    LocalDate today = LocalDate.now();
                    
                    for (Map<String, Object> taskData : tasksList) {
                        Task task = new Task();
                        task.setDescription((String) taskData.get("description"));
                        
                        User user = new User();
                        user.setId(userId);
                        task.setUser(user);
                        
                        String priorityStr = (String) taskData.get("priority");
                        if (priorityStr != null) {
                            task.setPriority(TaskPriority.valueOf(priorityStr.toLowerCase()));
                        } else {
                            task.setPriority(TaskPriority.media);
                        }
                        
                        String dueDateStr = (String) taskData.get("due_date");
                        String dueTimeStr = (String) taskData.get("due_time");
                        
                        if (dueDateStr != null && !dueDateStr.equals("null")) {
                            try {
                                String timeStr = "00:00:00"; // Default time
                                if (dueTimeStr != null && !dueTimeStr.equals("null")) {
                                    timeStr = dueTimeStr + ":00"; // Add seconds
                                }
                                task.setDueDate(LocalDateTime.parse(dueDateStr + "T" + timeStr));
                            } catch (Exception e) {
                                LocalDateTime relativeDate = calculateRelativeDueDate(dueDateStr);
                                if (relativeDate != null && dueTimeStr != null && !dueTimeStr.equals("null")) {
                                    String[] timeParts = dueTimeStr.split(":");
                                    int hour = Integer.parseInt(timeParts[0]);
                                    int minute = timeParts.length > 1 ? Integer.parseInt(timeParts[1]) : 0;
                                    task.setDueDate(relativeDate.withHour(hour).withMinute(minute).withSecond(0));
                                } else {
                                    task.setDueDate(relativeDate);
                                }
                            }
                        }
                        
                        Transcription transcription = new Transcription();
                        transcription.setId(transcriptionId);
                        task.setTranscription(transcription);
                        task.setCompleted(false);
                        
                        tasks.add(task);
                    }
                    
                    return tasks;
                }
            }
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private LocalDateTime calculateRelativeDueDate(String dueDateStr) {
        LocalDate today = LocalDate.now();
        String lowerDate = dueDateStr.toLowerCase();
        
        try {
            if (lowerDate.contains("mañana")) {
                return today.plusDays(1).atStartOfDay();
            } else if (lowerDate.contains("pasado mañana")) {
                return today.plusDays(2).atStartOfDay();
            } else if (lowerDate.contains("lunes")) {
                return getNextDayOfWeek(today, "monday").atStartOfDay();
            } else if (lowerDate.contains("martes")) {
                return getNextDayOfWeek(today, "tuesday").atStartOfDay();
            } else if (lowerDate.contains("miércoles") || lowerDate.contains("miercoles")) {
                return getNextDayOfWeek(today, "wednesday").atStartOfDay();
            } else if (lowerDate.contains("jueves")) {
                return getNextDayOfWeek(today, "thursday").atStartOfDay();
            } else if (lowerDate.contains("viernes")) {
                return getNextDayOfWeek(today, "friday").atStartOfDay();
            } else if (lowerDate.contains("sábado")) {
                return getNextDayOfWeek(today, "saturday").atStartOfDay();
            } else if (lowerDate.contains("domingo")) {
                return getNextDayOfWeek(today, "sunday").atStartOfDay();
            } else if (lowerDate.contains("próxima semana")) {
                return today.plusWeeks(1).atStartOfDay();
            }
        } catch (Exception e) {
            // Ignorar errores
        }
        
        return null;
    }

    private Integer extractTimeFromText(String text) {
        String lowerText = text.toLowerCase();
        
        // Buscar patrones como "a las X pm/am", "a las X", "X pm/am", etc.
        try {
            if (lowerText.contains("a las")) {
                String[] parts = lowerText.split("a las")[1].trim().split("\\s+");
                if (parts.length >= 2) {
                    int hour = Integer.parseInt(parts[0]);
                    String period = parts[1];
                    
                    if (period.contains("pm") && hour != 12) {
                        hour += 12;
                    } else if (period.contains("am") && hour == 12) {
                        hour = 0;
                    }
                    return hour;
                }
            }
            
            // Patrón para "X pm/am" sin "a las"
            String[] timePatterns = {
                "(\\d+)\\s*pm", "(\\d+)\\s*am",
                "(\\d+)\\s*de la tarde", "(\\d+)\\s*de la mañana",
                "(\\d+)\\s*de la noche"
            };
            
            for (String pattern : timePatterns) {
                if (lowerText.matches(".*" + pattern + ".*")) {
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                    java.util.regex.Matcher m = p.matcher(lowerText);
                    if (m.find()) {
                        int hour = Integer.parseInt(m.group(1));
                        
                        if (pattern.contains("pm") || pattern.contains("tarde") || pattern.contains("noche")) {
                            if (hour != 12) hour += 12;
                        } else if (pattern.contains("am") || pattern.contains("mañana")) {
                            if (hour == 12) hour = 0;
                        }
                        return hour;
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar errores de parseo
        }
        
        return null;
    }

    private LocalDate getNextDayOfWeek(LocalDate date, String dayName) {
        int targetDay = switch (dayName.toLowerCase()) {
            case "monday" -> 1;
            case "tuesday" -> 2;
            case "wednesday" -> 3;
            case "thursday" -> 4;
            case "friday" -> 5;
            case "saturday" -> 6;
            case "sunday" -> 7;
            default -> 1;
        };
        
        int currentDay = date.getDayOfWeek().getValue();
        int daysToAdd = (targetDay - currentDay + 7) % 7;
        if (daysToAdd == 0) daysToAdd = 7;
        
        return date.plusDays(daysToAdd);
    }
}
