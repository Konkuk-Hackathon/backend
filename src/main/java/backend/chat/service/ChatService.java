package backend.chat.service;

import backend.chat.repository.MemberRepository;
import backend.chat.repository.ThreadRepository;
import backend.domain.Member;
import backend.domain.Thread;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor

public class ChatService implements ChatUseCase{

    private final ChatClient chatClient;
    private final PromptBuilder promptBuilder;
    private final ThreadRepository threadRepository;
    private final MemberRepository memberRepository;

    @Override
    public Thread findThread(Member member, String conversationId, LocalDateTime chatSentTime) {
        return threadRepository.findActiveThread(member, conversationId)
                .orElseGet(()-> createThread(chatSentTime,member));
    }

    @Override
    public String sendChat(String conversationId, String message, Guest guest) {
        System.out.println("conversationId = " + conversationId);
        Prompt prompt = promptBuilder.buildPrompt(message, guest);
        return chatClient
                .prompt(prompt)
                .advisors(a -> a.param(CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    @Transactional
    @Override
    public void updateThread(Member member, Thread thread, LocalDateTime chatSentTime) {
        memberRepository.save(member);  // Thread 를 처음 생성하는 경우를 고려해서 member를 영속성 컨텍스트에 추가
        thread.updateLastActivityTime(chatSentTime);
        threadRepository.save(thread);
    }

    private Thread createThread(LocalDateTime createdTime, Member member){
        return Thread.builder()
                .conversationId(UUID.randomUUID().toString())
                .createdTime(createdTime)
                .member(member)
                .isActive(true)
                .build();
    }
}
