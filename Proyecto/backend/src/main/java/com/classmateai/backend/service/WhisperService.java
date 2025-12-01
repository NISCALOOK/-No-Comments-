package com.classmateai.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WhisperService {

    @Value("${whisper.api.key}")
    private String whisperApiKey;

    @Value("${storage.upload-dir}")
    private String uploadDir;

    private static final String WHISPER_SERVER = "grpc.nvcf.nvidia.com:443";
    private static final String FUNCTION_ID = "b702f636-f60c-4a3d-a6f4-f3568c13bd7d";

    public String transcribeAudio(File audioFile) throws IOException, InterruptedException {
        Path tempFilePath = audioFile.toPath();
        Path wavFilePath = tempFilePath;

        try {
            // Convertir a WAV si no es ya WAV
            String fileName = audioFile.getName().toLowerCase();
            if (!fileName.endsWith(".wav")) {
                wavFilePath = convertToWav(tempFilePath);
            }

            String projectRoot = System.getProperty("user.dir").replace("/Proyecto/backend", "").replace("\\Proyecto\\backend", "");
            
            // Detectar sistema operativo y construir paths apropiados
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
            String venvPath, pythonCommand, scriptPath;
            
            if (isWindows) {
                venvPath = projectRoot + "\\venv\\Scripts\\activate.bat";
                pythonCommand = "python";
                scriptPath = projectRoot + "\\python-clients\\scripts\\asr\\transcribe_file_offline.py";
            } else {
                venvPath = projectRoot + "/venv/bin/activate";
                pythonCommand = "python";
                scriptPath = projectRoot + "/python-clients/scripts/asr/transcribe_file_offline.py";
            }
            
            // Construir comando según el SO
            List<String> command = new ArrayList<>();
            if (isWindows) {
                command.add("cmd");
                command.add("/c");
                command.add(venvPath + " && " + pythonCommand + " " + scriptPath + " " +
                    "--server " + WHISPER_SERVER + " " +
                    "--use-ssl " +
                    "--metadata function-id " + FUNCTION_ID + " " +
                    "--metadata authorization \"Bearer " + whisperApiKey + "\" " +
                    "--language-code es " +
                    "--input-file \"" + wavFilePath.toString() + "\" " +
                    "--max-message-length 100000000");
            } else {
                command.add("bash");
                command.add("-c");
                command.add("source " + venvPath + " && " + pythonCommand + " " + scriptPath + " " +
                    "--server " + WHISPER_SERVER + " " +
                    "--use-ssl " +
                    "--metadata function-id " + FUNCTION_ID + " " +
                    "--metadata authorization \"Bearer " + whisperApiKey + "\" " +
                    "--language-code es " +
                    "--input-file " + wavFilePath.toString() + " " +
                    "--max-message-length 100000000");
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Esperar a que termine el proceso (máximo 10 minutos)
            boolean finished = process.waitFor(10, TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("La transcripción tomó demasiado tiempo");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException("Error en la transcripción: " + output.toString());
            }

            return extractTranscriptionText(output.toString());

        } finally {
            if (!wavFilePath.equals(tempFilePath)) {
                try {
                    Files.deleteIfExists(wavFilePath);
                } catch (IOException e) {
                    System.err.println("Error al eliminar archivo WAV temporal: " + e.getMessage());
                }
            }
        }
    }

    private Path convertToWav(Path inputFilePath) throws IOException, InterruptedException {
        String inputFileName = inputFilePath.getFileName().toString();
        String wavFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".wav";
        Path wavFilePath = inputFilePath.getParent().resolve(wavFileName);

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg", "-i", inputFilePath.toString(),
            "-ar", "16000", // Sample rate 16kHz
            "-ac", "1",     // Mono
            "-y",           // Sobreescribir archivo de salida
            wavFilePath.toString()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Esperar a que termine el proceso
        boolean finished = process.waitFor(2, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("La conversión a WAV tomó demasiado tiempo");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new RuntimeException("Error en la conversión a WAV: " + output.toString());
        }

        return wavFilePath;
    }

    private String extractTranscriptionText(String output) {
        try {
            int jsonStart = output.indexOf("{");
            int jsonEnd = output.lastIndexOf("}");
            
            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                String finalTranscriptMarker = "Final transcript: ";
                int finalIndex = output.lastIndexOf(finalTranscriptMarker);
                if (finalIndex != -1) {
                    return output.substring(finalIndex + finalTranscriptMarker.length()).trim();
                }
                return output.trim();
            }
            
            String jsonStr = output.substring(jsonStart, jsonEnd + 1);
            
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> response = mapper.readValue(jsonStr, Map.class);
            
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results == null || results.isEmpty()) {
                return output.trim();
            }
            
            StringBuilder transcription = new StringBuilder();
            for (Map<String, Object> result : results) {
                List<Map<String, Object>> alternatives = (List<Map<String, Object>>) result.get("alternatives");
                if (alternatives != null && !alternatives.isEmpty()) {
                    String transcript = (String) alternatives.get(0).get("transcript");
                    if (transcript != null && !transcript.trim().isEmpty()) {
                        if (transcription.length() > 0) {
                            transcription.append(" ");
                        }
                        transcription.append(transcript.trim());
                    }
                }
            }
            
            if (transcription.length() == 0) {
                String finalTranscriptMarker = "Final transcript: ";
                int finalIndex = output.lastIndexOf(finalTranscriptMarker);
                if (finalIndex != -1) {
                    return output.substring(finalIndex + finalTranscriptMarker.length()).trim();
                }
            }
            
            return transcription.toString().trim();
            
        } catch (Exception e) {
            String finalTranscriptMarker = "Final transcript: ";
            int finalIndex = output.lastIndexOf(finalTranscriptMarker);
            if (finalIndex != -1) {
                return output.substring(finalIndex + finalTranscriptMarker.length()).trim();
            }
            return output.trim();
        }
    }
}
