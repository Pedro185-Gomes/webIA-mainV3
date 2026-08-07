package com.example.webIA.Service;

import com.example.webIA.Model.Atendimento;
import com.example.webIA.Model.StatusAtendimento;
import com.example.webIA.Repository.AtendimentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageProcessorService {

    private final GroqService groqService;
    private final ZapService zapService;
    private final AtendimentoRepository atendimentoRepository;

    @Async
    public void process(String phone, String message, String senderName) {
        try {
            Atendimento atendimento = atendimentoRepository.findByPhone(phone)
                    .orElse(Atendimento.builder()
                            .phone(phone)
                            .nomeCliente(senderName)
                            .status(StatusAtendimento.NOVO_CONTATO)
                            .criadoEm(LocalDateTime.now())
                            .build());

            StatusAtendimento novoStatus = detectarStatus(message);
            if (novoStatus != null) {
                atendimento.setStatus(novoStatus);
            }

            atendimento.setUltimaMensagem(message);
            atendimento.setAtualizadoEm(LocalDateTime.now());
            atendimentoRepository.save(atendimento);

            int delay = 60000 + new Random().nextInt(60000);
            Thread.sleep(delay);

            String aiResponse = groqService.sendMessage(phone, message, senderName);

            String[] partes = aiResponse.split("\\|\\|\\|");
            for (String parte : partes) {
                String texto = parte.trim();
                if (!texto.isEmpty()) {
                    zapService.sendMessage(phone, texto);
                    Thread.sleep(3000 + new Random().nextInt(3000));
                }
            }

        } catch (Exception e) {
            log.error("Erro ao processar mensagem de {}: {}", phone, e.getMessage());
        }
    }

    private StatusAtendimento detectarStatus(String mensagem) {
        String lower = mensagem.toLowerCase();

        if (lower.contains("confirmo") || lower.contains("pode mandar") ||
                lower.contains("fechado") || lower.contains("vamos fechar") ||
                lower.contains("aprovado") || lower.contains("pode gerar") ||
                lower.contains("pode fazer o pedido")) {
            return StatusAtendimento.FECHADO_GANHO;
        }

        if (lower.contains("não tenho interesse") || lower.contains("não preciso") ||
                lower.contains("desisti") || lower.contains("cancela")) {
            return StatusAtendimento.SEM_INTERESSE;
        }

        if (lower.contains("orçamento") || lower.contains("quanto custa") ||
                lower.contains("preço") || lower.contains("valor")) {
            return StatusAtendimento.ORCAMENTO_EM_ANDAMENTO;
        }

        return null;
    }

}
