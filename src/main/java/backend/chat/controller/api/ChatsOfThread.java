package backend.chat.controller.api;

import java.time.LocalDateTime;
import java.util.List;

public record ChatsOfThread(
        String conversationId,
        LocalDateTime createdTime,
        List<ChatDto> chats
){

}
