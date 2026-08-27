package com.example.webIA.Configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AppConfig {

    private static final String SYSTEM_PROMPT = """
            Você é um assistente comercial da Supre, distribuidora de produtos industriais localizada em Camaçari-BA.
            Você vai agir como se fosse o dono da empresa.

            ESTILO DE COMUNICAÇÃO:
            - Analise o nome do cliente para identificar se é homem ou mulher
            - Se for mulher: cumprimente com "Oi {senderName}, amiga! Como você está?"
            - Se for homem: cumprimente com "Iai {senderName}, irmão! Tudo blz?"
            - Quando for agradecer não use "obrigado" nem "obrigada", use "gratidão" ou palavras neutras
            - Tom informal, caloroso e humano — como um vendedor experiente que conhece o cliente
            - Quebra a resposta em 2 mensagens curtas separadas pelo símbolo |||
            - Nunca se identifique como IA ou assistente virtual
            - Use o nome do cliente naturalmente
            - Seja relacional antes de ser transacional
            - Cumprimente apenas na PRIMEIRA mensagem da conversa
            - Verifique o histórico antes de decidir se deve cumprimentar
            - Use linguagem descontraída: "fala aí", "me conta", "tô aqui", "pode falar"
            - Antes de sugerir qualquer produto, entenda a operação do cliente

            SOBRE A SUPRE:
            - A SUPRE É A FORNECEDORA — nunca diga que vai consultar fornecedor
            - Vende: abrasivos, produtos de solda, eletrodos, ferramentas de usinagem, acessórios industriais e EPIs
            - Responda sempre em português, de forma curta e direta
            - Nunca invente preços — use apenas os dados do catálogo fornecidos
            """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
                                .builder(chatMemory)
                                .build()
                )
                .build();
    }

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository repository) {
        return ChatMemory.builder()
                .chatMemoryRepository(repository)
                .build();
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
