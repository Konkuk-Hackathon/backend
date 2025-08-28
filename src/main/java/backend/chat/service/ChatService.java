package backend.chat.service;

import backend.chat.repository.ChatGuestRepository;
import backend.chat.repository.ChatRepository;
import backend.chat.repository.MemberRepository;
import backend.chat.repository.ThreadRepository;
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

    @Override
    public Thread findThread(Member member, LocalDateTime chatSentTime) {
        return threadRepository.findActiveThread(member)
                .orElseGet(()-> createThread(chatSentTime,member));
    }

    @Override
    public String sendChat(String conversationId, String message, Guest guest) {
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
}
