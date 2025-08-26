package backend.chat.service;

public interface ChatUseCase {

    String sendChat(String conversationId, String message, Guest guest);

}
