package backend.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService implements ChatUseCase{

    private final ChatClient chatClient;
    private final PromptBuilder promptBuilder;

    @Override
    public String sendChat(String message, Guest guest) {
        Prompt prompt = promptBuilder.buildPrompt(message, guest);
        return chatClient.prompt(prompt).call().content();
    }
}
