package backend.domain;

import backend.chat.service.Guest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChatGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_guest_id")
    private Long id;
    private String conversationId;
    private LocalDateTime timeStamp;

    @Enumerated(EnumType.STRING)
    private Guest guest;
}
