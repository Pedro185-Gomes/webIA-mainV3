package com.example.webIA.Service;

import com.example.webIA.Model.Conversa;
import com.example.webIA.Repository.ConversaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroqService {

    private final ConversaRepository conversaRepository;
    private final ProdutoService produtoService;

    @Value("${groq.api-key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;

    private final RestClient restClient = RestClient.create();

    private static final String SYSTEM_PROMPT = """
            Você é um assistente comercial da Supre, distribuidora de produtos industriais localizada em Camaçari-BA.
            
             ESTILO DE COMUNICAÇÃO:
                        - Escreva de forma natural e humana, como um vendedor experiente
                        - Mensagens curtas — máximo 3 linhas por resposta
                        - Nunca use colchetes, códigos ou termos técnicos desnecessários
                        - Use o nome do cliente naturalmente na conversa
                        - Seja direto e objetivo
            
        A SUPRE É A FORNECEDORA — nunca diga que vai consultar fornecedor, pois nós somos o fornecedor.
        A Supre vende: abrasivos, produtos de solda, eletrodos, ferramentas de usinagem, acessórios industriais e EPIs.
        Quando o cliente pedir um produto, use as informações do catálogo fornecidas para montar o orçamento diretamente.
        Se o produto estiver no catálogo, informe código, descrição, preço unitário e calcule o total pela quantidade pedida.
        Se o produto não estiver no catálogo, diga que não temos esse item disponível e sugira alternativas similares.
        Responda sempre em português, de forma curta e direta.
        Nunca invente preços — use apenas os dados do catálogo fornecidos.
        """;

    public String sendMessage(String phone, String userMessage, String senderName) {
        // salva mensagem do usuário no banco
        conversaRepository.save(Conversa.builder()
                .phone(phone)
                .role("user")
                .content(userMessage)
                .data(LocalDateTime.now())
                .build());

        // busca histórico do banco
        List<Conversa> historico = conversaRepository.findByPhoneOrderByDataAsc(phone);

        // mantém só as últimas 20
        if (historico.size() > 20) {
            historico = historico.subList(historico.size() - 20, historico.size());
        }

        // monta lista de mensagens para o Groq

        String produtosEncontrados = produtoService.buscarProdutos(userMessage);

        String promptFinal = SYSTEM_PROMPT;
        if (produtosEncontrados != null) {
            promptFinal += "\n\nInformações do catálogo da Supre para esta mensagem:\n" + produtosEncontrados;
        }

        Map<String, Object> systemMessage = Map.of("role", "system", "content", promptFinal + "\nVocê está conversando com " + senderName + ". Use o nome dele nas respostas quando apropriado.");

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(systemMessage);

        for (Conversa c : historico) {
            messages.add(Map.of("role", c.getRole(), "content", c.getContent()));
        }

        Map<String, Object> body = Map.of("model", model, "messages", messages);

        try {
            Map response = restClient.post()
                    .uri("https://api.groq.com/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            List<Map> choices = (List<Map>) response.get("choices");

            String aiResponse = (String) ((Map) choices.get(0).get("message")).get("content");

            // salva resposta da IA no banco
            conversaRepository.save(Conversa.builder()
                    .phone(phone)
                    .role("assistant")
                    .content(aiResponse)
                    .data(LocalDateTime.now())
                    .build());

            return aiResponse;

        } catch (Exception e) {
            log.error("Erro ao chamar Groq: {}", e.getMessage());
            return "Desculpe, ocorreu um erro ao processar sua mensagem.";
        }
    }
}