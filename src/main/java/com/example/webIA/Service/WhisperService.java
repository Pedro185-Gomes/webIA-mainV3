package com.example.webIA.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class WhisperService {

    @Value("${groq.api-key}")
    private String apiKey;

    public String transcribe(String audioUrl) {
        try {
            // baixa o arquivo de áudio
            URL url = new URL(audioUrl);
            byte[] audioBytes = url.openStream().readAllBytes();

            // monta o multipart para o Whisper
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();

            HttpClient client = HttpClient.newHttpClient();

            byte[] filenamePart = ("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"audio.ogg\"\r\nContent-Type: audio/ogg\r\n\r\n").getBytes();
            byte[] modelPart = ("\r\n--" + boundary + "\r\nContent-Disposition: form-data; name=\"model\"\r\n\r\nwhisper-large-v3\r\n--" + boundary + "--\r\n").getBytes();

            byte[] body = new byte[filenamePart.length + audioBytes.length + modelPart.length];
            System.arraycopy(filenamePart, 0, body, 0, filenamePart.length);
            System.arraycopy(audioBytes, 0, body, filenamePart.length, audioBytes.length);
            System.arraycopy(modelPart, 0, body, filenamePart.length + audioBytes.length, modelPart.length);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/audio/transcriptions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Whisper response: {}", response.body());

            // parseia a resposta
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.body());
            return json.get("text").asText();

        } catch (Exception e) {
            log.error("Erro ao transcrever áudio: {}", e.getMessage());
            return null;
        }
    }
}

