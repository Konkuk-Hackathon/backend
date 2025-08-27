package backend.chat.repository;

import backend.domain.Chat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Transactional
@SpringBootTest
class ChatRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ChatRepository chatRepository;
    @BeforeEach
    void setup() {
        // Prepare sample data in the test database.
        jdbcTemplate.update(
                "INSERT INTO SPRING_AI_CHAT_MEMORY (conversation_id, content, type, timestamp) VALUES (?, ?, ?, ?)",
                "conv1", "Hello", "USER", LocalDateTime.now()
        );
    }

    @Test
    void testFindByConversationId_Found() {
        List<Chat> chats = chatRepository.findByConversationId("conv1");
        assertEquals(1, chats.size());
        assertEquals("conv1", chats.get(0).conversationId());
    }

    @Test
    void testFindByConversationId_NotFound() {
        List<Chat> chats = chatRepository.findByConversationId("conv2");
        assertTrue(chats.isEmpty());
    }
}