package backend.chat.service;

import backend.chat.repository.*;
import backend.chat.service.dto.ChatsOfThreadDto;
import backend.domain.Chat;
import backend.domain.ChatGuest;
import backend.domain.Member;
import backend.domain.Thread;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor

class ChatService implements ChatUseCase{

    private final ChatClient chatClient;
    private final PromptBuilder promptBuilder;
    private final ThreadRepository threadRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatGuestRepository chatGuestRepository;
    private final ChatVectorStore chatVectorStore;

    @Override
    public Thread findThread(Member member, LocalDateTime chatSentTime) {
        return threadRepository.findActiveThread(member)
                .orElseGet(()-> createThread(chatSentTime,member));
    }

    @Transactional
    @Override
    public String sendChat(String conversationId, String message, Guest guest) {
        Prompt prompt = promptBuilder.buildPrompt(message, guest);
        String content = chatClient
                .prompt(prompt)
                .advisors(a -> a.param(CONVERSATION_ID, conversationId))
                .call()
                .content();
        this.saveChatGuest(conversationId, guest);
        return content;
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
                .lastActivityTime(createdTime)
                .member(member)
                .isActive(true)
                .build();
    }

    @Override
    public List<Thread> findThreadsOfMember(Member member) {
        return threadRepository.findAllByMember(member);
    }

    @Override
    public ChatsOfThreadDto findChatsOfThread(Thread thread) {
        List<Chat> byConversationId = chatRepository.findByConversationId(thread.getConversationId());
        List<ChatGuest> allByConversationId = chatGuestRepository.findAllByConversationId(thread.getConversationId());
        return new ChatsOfThreadDto(byConversationId, allByConversationId);
    }

    @Transactional
    @Override
    public void saveChatGuest(String conversationId, Guest guest) {
        List<Chat> latestTwoMessages = chatRepository.findLatestTwoByConversationId(conversationId);
        List<ChatGuest> chatGuests = latestTwoMessages.stream()
                .map(chat -> ChatGuest.builder()
                        .guest(guest)
                        .conversationId(chat.conversationId())
                        .timeStamp(chat.timestamp())
                        .build())
                .toList();
        chatGuestRepository.saveAll(chatGuests);

    }

    @Transactional
    @Override
    public void summary(Member member) {
        Thread thread = threadRepository.findActiveThread(member).orElseThrow();
        List<Chat> chats = chatRepository.findByConversationId(thread.getConversationId());
        StringBuilder conversation = new StringBuilder();
        for (Chat chat : chats) {
            conversation.append("type = ").append(chat.type()).append('\n')
                    .append("content = ").append(chat.content()).append('\n');
        }
        Prompt prompt = promptBuilder.buildSummaryTemplate(conversation.toString());
        String summary = chatClient.prompt(prompt).call().content();
        chatVectorStore.saveSummary(summary,member.getId(),thread.getLastActivityTime());
        thread.inactive();
    }
}
