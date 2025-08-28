package backend.chat.controller;

import backend.chat.controller.api.*;
import backend.chat.repository.MemberRepository;
import backend.chat.service.ChatUseCase;
import backend.chat.service.Guest;
import backend.chat.service.dto.ChatsOfThreadDto;
import backend.domain.Chat;
import backend.domain.ChatGuest;
import backend.domain.Member;
import backend.domain.Thread;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "채팅 API", description = "채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatUseCase chatUseCase;
    private final MemberRepository memberRepository;
    private final Long defaultMemberId = 1L;

    @Operation(summary = "게스트 코드 목록 조회", description = "채팅 가능한 게스트(AI)의 코드와 이름 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = GuestCodeResponse.class)))
    })
    @GetMapping("/guest-codes")
    public ResponseEntity<GuestCodeResponse> getGuestCodes() {
        return ResponseEntity.ok().body(new GuestCodeResponse(
                Arrays.stream(Guest.values())
                        .map(guestEnum -> new GuestCodeResponse.GuestCode(guestEnum.getGuestName(), guestEnum.getGuestCode()))
                        .toList()));
    }

    @Operation(summary = "채팅 메시지 전송", description = """
    채팅 메시지를 AI 게스트에게 전달하고 응답을 반환합니다.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전송 성공",
                    content = @Content(schema = @Schema(implementation = ChatResponse.class)))
    })
    @PostMapping("/chats")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        Guest guest = Guest.fromCode(request.getGuestCode());
        LocalDateTime chatSentTime = LocalDateTime.now();
        Member member = memberRepository.findById(defaultMemberId).orElseThrow(); // service로 감아야하는데 나중에
        Thread thread = chatUseCase.findThread(member, chatSentTime);
        String chatResponse = chatUseCase.sendChat(thread.getConversationId(), request.getMessage(), guest);
        chatUseCase.updateThread(member, thread, chatSentTime);
        chatUseCase.saveChatGuest(thread.getConversationId(), guest);
        return ResponseEntity.ok().body(new ChatResponse(chatResponse, chatSentTime));
    }

    @Operation(summary = "모든 채팅 대화 조회",
            description = """
        사용자의 모든 채팅 대화(스레드) 목록을 반환합니다.
        정렬 규칙
        - 스레드(Thread) 목록은 생성 시각(createdTime) 기준 오름차순으로 정렬되어 반환됩니다.
          → 가장 먼저 생성된 대화 스레드가 맨 앞에 위치합니다.
        - 각 스레드에 속한 채팅 메시지(Chat)는 전송 시각(timestamp) 기준 오름차순으로 정렬됩니다.
          → 대화 시작 메시지가 배열의 첫 번째에 위치하며, 시간 흐름에 따라 순차적으로 정렬됩니다""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ThreadsOfMember.class))
            )
    })
    @GetMapping("/chats")
    public ResponseEntity<ThreadsOfMember> getAllMessagesOfMember(){
        Member member = memberRepository.findById(defaultMemberId).orElseThrow();
        List<ChatsOfThread> chatsOfThreads = new ArrayList<>();
        List<Thread> threadsOfMember = chatUseCase.findThreadsOfMember(member);
        for (Thread thread : threadsOfMember) {
            ChatsOfThreadDto chatsOfThread = chatUseCase.findChatsOfThread(thread);
            List<ChatDto> list = toChatDtos(chatsOfThread.chats(), chatsOfThread.chatGuests());
            chatsOfThreads.add(new ChatsOfThread(thread.getConversationId(), thread.getCreatedTime(), list));
        }
        return ResponseEntity.ok().body(new ThreadsOfMember(chatsOfThreads));
    }

    public List<ChatDto> toChatDtos(List<Chat> chats, List<ChatGuest> chatGuests) {
        if (chats.size() != chatGuests.size()) {
            System.out.println("chats.size() = " + chats.size());
            System.out.println("chatGuests.size() = " + chatGuests.size());
            throw new IllegalArgumentException("Chats and ChatGuests size must be the same");
        }

        List<ChatDto> result = new ArrayList<>();

        for (int i = 0; i < chats.size(); i++) {
            Chat chat = chats.get(i);
            ChatGuest chatGuest = chatGuests.get(i);

            // Guest → guestCode 추출
            String guestCode = chatGuest.getGuest().getGuestCode();

            ChatDto dto = new ChatDto(
                    chat.content(),
                    chat.type(),
                    guestCode,
                    chat.timestamp()
            );

            result.add(dto);
        }

        return result;
    }

}