package backend.chat.service;


import backend.chat.service.dto.ChatsOfThreadDto;
import backend.domain.Chat;
import backend.domain.Member;
import backend.domain.Thread;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatUseCase {

    Thread findThread(Member member, LocalDateTime updateTime);

    String sendChat(String conversationId, String message, Guest guest);

    void updateThread(Member member, Thread thread, LocalDateTime chatSentTime);

    List<Thread> findThreadsOfMember(Member member);

    ChatsOfThreadDto findChatsOfThread(Thread thread);

    void saveChatGuest(String conversationId, Guest guest);

    void summary(Member member);
}
