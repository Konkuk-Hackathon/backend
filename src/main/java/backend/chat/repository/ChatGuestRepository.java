package backend.chat.repository;

import backend.domain.ChatGuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatGuestRepository  extends JpaRepository<ChatGuest, Long> {

    @Query("select c from ChatGuest c where c.conversationId = :conversationId")
    List<ChatGuest> findAllByConversationId(String conversationId);

}
