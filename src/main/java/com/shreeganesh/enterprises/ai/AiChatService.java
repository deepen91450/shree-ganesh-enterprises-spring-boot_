package com.shreeganesh.enterprises.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("ai")
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private static final String SYSTEM_PROMPT = """
            You are an internal administrative assistant for Shree Ganesh Enterprises.
            Answer the user's question concisely. You do not have access to product, customer,
            order, enquiry, user, or system data. Do not claim to have read data or performed an action.
            """;

    private final ChatClient chatClient;

    public AiChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String ask(String message) {
        try {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(message)
                    .call()
                    .content();
        } catch (RuntimeException ex) {
            log.warn("Ollama chat request failed: {}", ex.getClass().getSimpleName());
            throw new AiProviderUnavailableException(ex);
        }
    }
}
