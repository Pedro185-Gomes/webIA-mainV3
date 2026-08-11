package com.example.webIA.Controller;

import com.example.webIA.Service.GroqService;
import com.example.webIA.Service.MessageProcessorService;
import com.example.webIA.Service.WhisperService;
import com.example.webIA.Service.ZapService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
@Slf4j
@RequiredArgsConstructor
public class WhatsappController {

    private final ZapService service;
    private final GroqService groqService;
    private final WhisperService whisperService;
    private final MessageProcessorService messageProcessorService;

    @Value("${ia.numeros-permitidos}")
    private String numerosPermitidosConfig;

    @PostMapping
    public ResponseEntity<Void> receiveMessage(@RequestBody Map<String, Object> payload) {
        log.info("Webhook recebido: {}", payload);

        String type = (String) payload.get("type");
        if (!"ReceivedCallback".equals(type)) {
            return ResponseEntity.ok().build();
        }

        Boolean fromMe = (Boolean) payload.get("fromMe");
        if (Boolean.TRUE.equals(fromMe)) {
            return ResponseEntity.ok().build();
        }

        String phone = (String) payload.get("phone");

        List<String> numerosPermitidos = Arrays.asList(numerosPermitidosConfig.split(","));
        if (!numerosPermitidos.contains(phone)) {
            log.info("Número {} não está na lista de permitidos. Ignorando.", phone);
            return ResponseEntity.ok().build();
        }

        Map<String, Object> text = (Map<String, Object>) payload.get("text");
        Map<String, Object> audio = (Map<String, Object>) payload.get("audio");

        String message;

        if (text != null) {
            message = (String) text.get("message");
            log.info("Mensagem de texto de {}: {}", phone, message);
        } else if (audio != null) {
            String audioUrl = (String) audio.get("audioUrl");
            log.info("Áudio recebido de {}: {}", phone, audioUrl);
            message = whisperService.transcribe(audioUrl);
            if (message == null || message.isBlank()) {
                service.sendMessage(phone, "Não consegui entender o áudio. Pode repetir em texto?");
                return ResponseEntity.ok().build();
            }
            log.info("Transcrição do áudio: {}", message);
        } else {
            service.sendMessage(phone, "Por enquanto só processo texto e áudio.");
            return ResponseEntity.ok().build();
        }

        String senderName = (String) payload.get("senderName");
        messageProcessorService.process(phone, message, senderName);

        return ResponseEntity.ok().build();

    }
}
