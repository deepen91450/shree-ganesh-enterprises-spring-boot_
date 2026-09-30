package com.shreeganesh.enterprises.ai;

import com.shreeganesh.enterprises.ai.dto.AiChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AiChatController.class)
public class AiApiExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AiChatResponse> handleUnreadableRequest() {
        return ResponseEntity.badRequest().body(AiChatResponse.error("A valid JSON request is required."));
    }
}
