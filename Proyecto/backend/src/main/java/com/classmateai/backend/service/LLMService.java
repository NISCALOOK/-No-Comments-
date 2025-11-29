package com.classmateai.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LLMService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${llm.api.url}")
    private String llmApiUrl;

    @Value("${llm.api.key}")
    private String llmApiKey;

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
            "max_tokens", 1000
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
            "max_tokens", 200
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

    public String chatWithLLM(String message, String context) {
        String prompt = buildChatPrompt(message, context);
        
        Map<String, Object> request = Map.of(
            "model", "minimax-m2-free",
            "messages", List.of(
                Map.of("role", "system", "content", "Eres un asistente académico especializado en ayudar a estudiantes con sus clases y apuntes."),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.5,
            "max_tokens", 800
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

            return parseChatResponse(response);
            
        } catch (Exception e) {
            throw new RuntimeException("Error en el chat con LLM: " + e.getMessage(), e);
        }
    }

    private String buildSummaryPrompt(String fullText) {
        return String.format("""
            Analiza el siguiente texto de una clase transcrita y genera:
            
            1. Un título conciso y descriptivo (máximo 60 caracteres)
            2. Un resumen estructurado con los puntos clave
            
            Responde en formato JSON:
            {
              "title": "título aquí",
              "summary": "resumen aquí"
            }
            
            Texto a analizar:
            %s
            """, fullText);
    }

    private String buildTagsPrompt(String fullText, String summary) {
        return String.format("""
            Basado en el siguiente texto y resumen de una clase, genera 5-7 etiquetas relevantes que describan el contenido.
            Las etiquetas deben ser conceptos clave, temas importantes o palabras descriptivas.
            
            Responde solo con las etiquetas separadas por comas, sin formato JSON.
            
            Texto: %s
            
            Resumen: %s
            """, fullText.substring(0, Math.min(1000, fullText.length())), summary);
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
                    // Intentar parsear como JSON para obtener title y summary
                    try {
                        JsonNode jsonContent = objectMapper.readTree(content);
                        String title = jsonContent.get("title").asText();
                        String summary = jsonContent.get("summary").asText();
                        return String.format("{\"title\":\"%s\",\"summary\":\"%s\"}", 
                            title.replace("\"", "\\\""), summary.replace("\"", "\\\""));
                    } catch (Exception e) {
                        // Si no es JSON, devolver el contenido como está
                        return content;
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
                    // Parsear etiquetas separadas por comas
                    List<String> tags = new ArrayList<>();
                    String[] tagArray = content.split(",");
                    for (String tag : tagArray) {
                        String cleanTag = tag.trim().replaceAll("[^\\w\\sáéíóúÁÉÍÓÚñÑ-]", "");
                        if (!cleanTag.isEmpty()) {
                            tags.add(cleanTag);
                        }
                    }
                    return tags;
                }
            }
            throw new RuntimeException("Respuesta inválida del LLM para etiquetas");
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear etiquetas del LLM: " + e.getMessage(), e);
        }
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
}