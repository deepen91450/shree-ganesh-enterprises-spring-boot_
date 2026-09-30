package com.shreeganesh.enterprises.ai.dto;

public record AiChatResponse(String answer, String error) {

    public static AiChatResponse success(String answer) {
        return new AiChatResponse(answer, null);
    }

    public static AiChatResponse error(String error) {
        return new AiChatResponse(null, error);
    }
}
