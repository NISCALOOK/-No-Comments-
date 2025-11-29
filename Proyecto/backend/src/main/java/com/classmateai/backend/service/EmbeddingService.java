package com.classmateai.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    @Value("${embeddings.api.key}")
    private String embeddingsApiKey;

    @Value("${embeddings.api.url}")
    private String embeddingsApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String MODEL = "nvidia/llama-3.2-nv-embedqa-1b-v2";
    private static final int EMBEDDING_DIMENSION = 2048; // Dimensión del modelo

    public float[] generateEmbedding(String text, String inputType) {
        try {
            // Construir el request body
            String requestBody = String.format("""
                {
                    "input": ["%s"],
                    "model": "%s",
                    "input_type": "%s",
                    "encoding_format": "float",
                    "truncate": "NONE"
                }
                """, escapeJson(text), MODEL, inputType);

            // Configurar headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setBearerAuth(embeddingsApiKey);

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);

            // Hacer la llamada a la API
            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(
                embeddingsApiUrl, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error en llamada a API de embeddings: " + response.getStatusCode());
            }

            // Parsear la respuesta
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode dataNode = rootNode.path("data");
            
            if (dataNode.isArray() && dataNode.size() > 0) {
                JsonNode embeddingNode = dataNode.get(0).path("embedding");
                if (embeddingNode.isArray()) {
                    float[] embedding = new float[embeddingNode.size()];
                    for (int i = 0; i < embeddingNode.size(); i++) {
                        embedding[i] = (float) embeddingNode.get(i).asDouble();
                    }
                    return embedding;
                }
            }

            throw new RuntimeException("No se pudo extraer el embedding de la respuesta");

        } catch (Exception e) {
            throw new RuntimeException("Error generando embedding: " + e.getMessage(), e);
        }
    }

    public float[] generateQueryEmbedding(String query) {
        return generateEmbedding(query, "query");
    }

    public float[] generateDocumentEmbedding(String document) {
        return generateEmbedding(document, "passage");
    }

    public List<float[]> generateBatchEmbeddings(List<String> texts, String inputType) {
        List<float[]> embeddings = new ArrayList<>();
        
        // Procesar en lotes para evitar límites de la API
        int batchSize = 10;
        for (int i = 0; i < texts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, texts.size());
            List<String> batch = texts.subList(i, end);
            
            try {
                // Construir request para batch
                StringBuilder jsonInputs = new StringBuilder();
                for (int j = 0; j < batch.size(); j++) {
                    if (j > 0) jsonInputs.append(", ");
                    jsonInputs.append("\"").append(escapeJson(batch.get(j))).append("\"");
                }

                String requestBody = String.format("""
                    {
                        "input": [%s],
                        "model": "%s",
                        "input_type": "%s",
                        "encoding_format": "float",
                        "truncate": "NONE"
                    }
                    """, jsonInputs.toString(), MODEL, inputType);

                // Configurar headers
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                headers.setBearerAuth(embeddingsApiKey);

                org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);

                // Hacer la llamada a la API
                org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(
                    embeddingsApiUrl, entity, String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Error en llamada batch a API de embeddings: " + response.getStatusCode());
                }

                // Parsear la respuesta
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("data");
                
                if (dataNode.isArray()) {
                    for (JsonNode item : dataNode) {
                        JsonNode embeddingNode = item.path("embedding");
                        if (embeddingNode.isArray()) {
                            float[] embedding = new float[embeddingNode.size()];
                            for (int k = 0; k < embeddingNode.size(); k++) {
                                embedding[k] = (float) embeddingNode.get(k).asDouble();
                            }
                            embeddings.add(embedding);
                        }
                    }
                }

            } catch (Exception e) {
                // Si falla el batch, procesar individualmente
                for (String text : batch) {
                    embeddings.add(generateEmbedding(text, inputType));
                }
            }
        }
        
        return embeddings;
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    public int getEmbeddingDimension() {
        return EMBEDDING_DIMENSION;
    }
}