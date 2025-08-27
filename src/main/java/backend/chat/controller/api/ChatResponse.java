package backend.chat.controller.api;

import java.time.LocalDateTime;

public record ChatResponse(String response, LocalDateTime timeStamp, String conversationId) {
}
