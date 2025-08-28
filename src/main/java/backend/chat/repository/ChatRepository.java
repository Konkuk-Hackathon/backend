package backend.chat.repository;

import backend.domain.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final JdbcTemplate jdbc;

    public List<Chat> findByConversationId(String conversationId) {
        String sql = """
            SELECT conversation_id, content, type, timestamp
            FROM SPRING_AI_CHAT_MEMORY
            WHERE conversation_id = ?
            ORDER BY timestamp
        """;
        return jdbc.query(sql, (rs, i) -> new Chat(
                rs.getString("conversation_id"),
                rs.getString("content"),
                rs.getString("type"),
                rs.getTimestamp("timestamp").toLocalDateTime()
        ), conversationId);
    }

    public List<Chat> findLatestTwoByConversationId(String conversationId) {
        String sql = """
        SELECT conversation_id, content, type, timestamp
        FROM spring_ai_chat_memory
        WHERE conversation_id = ?
        ORDER BY timestamp DESC
        LIMIT 2 """;

        return jdbc.query(sql, (rs, i) -> new Chat(
                rs.getString("conversation_id"),
                rs.getString("content"),
                rs.getString("type"),
                rs.getTimestamp("timestamp").toLocalDateTime()
        ), conversationId);
    }


}
