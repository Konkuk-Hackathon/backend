package backend.chat.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PromptBuilder {

    private final ResourceLoader resourceLoader;

    private final Map<Guest, Resource> templates = new EnumMap<>(Guest.class);

    private PromptTemplate summaryTemplate;

    @Value("classpath:/prompts/summary-template.st")
    private Resource summaryPromptTemplate;

    @PostConstruct
    private void init() {
        for (Guest guest : Guest.values()) {
            Resource res = resourceLoader.getResource(guest.getTemplateLocation());
            templates.put(guest, res);
        }
        summaryTemplate = new PromptTemplate(summaryPromptTemplate);
    }

    public Prompt buildPrompt(String userMessage, Guest guest, List<String> documents) {
        List<Message> promptMessages = new ArrayList<>();
        promptMessages.add(new SystemMessage(templates.get(guest)));
        promptMessages.add(new UserMessage(userMessage));
        StringBuilder summary = new StringBuilder();
        summary.append("이건 RAG로 사용자의 메시지와 비슷한 이전 대화기록이야 ").append('\n');
        documents.forEach(document -> summary.append(document).append('\n'));
        promptMessages.add(new SystemMessage(summary.toString()));
        return new Prompt(promptMessages);
    }

    public Prompt buildSummaryTemplate(String conversation){
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("conversation", conversation);
        return summaryTemplate.create(promptParameters);
    }

}
