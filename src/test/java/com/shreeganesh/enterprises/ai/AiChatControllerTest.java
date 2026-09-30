package com.shreeganesh.enterprises.ai;

import com.shreeganesh.enterprises.ai.dto.AiChatRequest;
import com.shreeganesh.enterprises.ai.dto.AiChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiChatControllerTest {

    private final AiChatService aiChatService = mock(AiChatService.class);
    private final AiChatController controller = new AiChatController(aiChatService);

    @Test
    void rejectsBlankMessagesWithoutCallingTheProvider() {
        ResponseEntity<AiChatResponse> response = controller.chat(new AiChatRequest("  "));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error()).isEqualTo("A message is required.");
    }

    @Test
    void returnsModelAnswer() {
        when(aiChatService.ask("Summarize this")).thenReturn("Summary");

        ResponseEntity<AiChatResponse> response = controller.chat(new AiChatRequest(" Summarize this "));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().answer()).isEqualTo("Summary");
        verify(aiChatService).ask("Summarize this");
    }

    @Test
    void returnsSafeServiceUnavailableResponse() {
        doThrow(new AiProviderUnavailableException(new RuntimeException()))
                .when(aiChatService).ask("Hello");

        ResponseEntity<AiChatResponse> response = controller.chat(new AiChatRequest("Hello"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().error()).contains("currently unavailable");
    }
}
