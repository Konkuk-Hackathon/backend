package backend.chat.controller.api;

import java.time.LocalDateTime;

public record ChatDto (
        String content,
        String type,
        LocalDateTime timeStamp

){}


