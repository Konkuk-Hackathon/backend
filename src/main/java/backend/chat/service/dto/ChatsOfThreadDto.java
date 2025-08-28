package backend.chat.service.dto;

import backend.domain.Chat;
import backend.domain.ChatGuest;

import java.util.List;

public record ChatsOfThreadDto (List<Chat> chats, List<ChatGuest> chatGuests) {
}
