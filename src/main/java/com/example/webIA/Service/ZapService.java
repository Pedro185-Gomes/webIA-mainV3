package com.example.webIA.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Service
@Slf4j
public class ZapService {

    @Value("${zapi.instance-id}")
    private String instanceId;

    @Value("${zapi.token}")
    private String token;

    @Value("${zapi.client-token}")
    private String clientToken;

    private final RestClient restClient = RestClient.create();

    public void sendMessage(String phone, String message) {

        String url = "https://api.z-api.io/instances/" + instanceId + "/token/" + token + "/send-text";

        log.info("Enviando para URL: {}", url);
        log.info("Phone: {}, Message: {}", phone, message);

        Map<String, String> body = Map.of(
                "phone", phone,
                "message", message
        );

        try {
            restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Client-Token", clientToken)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Mensagem enviada para {}", phone);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para {}: {}", phone, e.getMessage());
        }
    }
}