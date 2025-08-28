package backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * 비즈니스 규칙 정리
 * 1. conversationId -> 이 Thread에 속한 chat을 조회해오는 필드 -> UUID
 * 2. lastActivityTime 기준이 "현재시점"으로부터 1시간 이상 넘어가면
 *    사용자가 추가적인 메시지를 전송하지 않는다고 판단하고 요약 작업 수행
 * 3. 요약 작업을 수행하고 isActive를 false로 변경
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_id")
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String conversationId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime lastActivityTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Boolean isActive;

    public void updateLastActivityTime(LocalDateTime updateTime) {
        this.lastActivityTime = updateTime;
    }

    public void inactive() {
        this.isActive = false;
    }

}
