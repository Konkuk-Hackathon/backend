package backend.chat.service;


import backend.domain.Member;
import backend.domain.Thread;

import java.time.LocalDateTime;

public interface ChatUseCase {

    Thread findThread(Member member, String conversationId, LocalDateTime updateTime);

    String sendChat(String conversationId, String message, Guest guest);

    void updateThread(Member member, Thread thread, LocalDateTime chatSentTime);


}
