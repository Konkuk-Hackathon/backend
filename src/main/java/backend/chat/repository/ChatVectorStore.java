package backend.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatVectorStore {

    private static final int TOP_K = 2;

    private final VectorStore vectorStore;

    public List<Document> searchSummaries(Long memberId, String query) {
        return vectorStore.similaritySearch(SearchRequest
                .builder()
                .query(query)
                .topK(TOP_K)
                .filterExpression("memberId == " + memberId)
                .build());
    }

    public void saveSummary(String summaryText,
                            Long memberId, LocalDateTime timestamp) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("memberId", memberId);
        meta.put("timestamp", timestamp); // summary date
        Document doc = new Document(summaryText, meta);
        vectorStore.add(List.of(doc));

    }

}
