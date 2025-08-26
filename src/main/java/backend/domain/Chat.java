package backend.domain;


import java.time.LocalDateTime;


/**
 * Chat 이라는 클래스는 Thread 에 속한 채팅을 의미함.
 * 즉, Thread - Chat 은 1:N의 관계를 맺음
 * 그런데, Chat에 JPA 를 이용해서 사용하지 않는 이유는 아래와 같음
 * 1. Spring AI가 메시지를 메모이제이션 하기 위한 테이블인, spring_ai_chat_memory 테이블은 PK 존재 X
 * 2. 또한, Spring AI 가 테이블을 직접 생성하고 관리하기에 커스터마이징이 까다로움
 * 3. 따라서 PK가 반드시 필요한 JPA 대신, JDBC 를 직접 사용하는게 낫다고 판단
 * 4. 추가적으로, Select & Delete 쿼리만 필요하므로 오히려 더 간단한 상황
 * */
public record Chat(
        String conversationId, // UUID
        String content, //message
        String type, // User, Assistant
        LocalDateTime timestamp
) {}
