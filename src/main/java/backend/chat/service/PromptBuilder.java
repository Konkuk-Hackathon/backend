package backend.chat.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PromptBuilder {

    private final ResourceLoader resourceLoader;

    private final Map<Guest, Resource> templates = new EnumMap<>(Guest.class);

    @PostConstruct
    private void init() {
        for (Guest guest : Guest.values()) {
            Resource res = resourceLoader.getResource(guest.getTemplateLocation());
            templates.put(guest, res);
        }
    }

    public Prompt buildPrompt(String userMessage, Guest guest) {
        return new Prompt(List.of(
                new SystemMessage(templates.get(guest)),
                new UserMessage(userMessage)
        ));
    }

}
