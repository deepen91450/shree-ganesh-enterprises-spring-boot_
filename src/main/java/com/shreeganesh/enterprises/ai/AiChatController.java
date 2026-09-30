package com.shreeganesh.enterprises.ai;

import com.shreeganesh.enterprises.ai.dto.AiChatRequest;
import com.shreeganesh.enterprises.ai.dto.AiChatResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("ai")
@RequestMapping("/admin/ai")
public class AiChatController {

    private static final int MAX_MESSAGE_LENGTH = 2_000;

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody(required = false) AiChatRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(AiChatResponse.error("A message is required."));
        }

        String message = request.message().trim();
        if (message.length() > MAX_MESSAGE_LENGTH) {
            return ResponseEntity.badRequest().body(
                    AiChatResponse.error("Message must not exceed " + MAX_MESSAGE_LENGTH + " characters.")
            );
        }

        try {
            return ResponseEntity.ok(AiChatResponse.success(aiChatService.ask(message)));
        } catch (AiProviderUnavailableException ex) {
            return ResponseEntity.status(503).body(
                    AiChatResponse.error("The AI service is currently unavailable. Please try again later.")
            );
        }
    }
}
