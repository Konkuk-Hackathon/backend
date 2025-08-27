package backend.chat.controller;

import backend.chat.controller.api.ChatRequest;
import backend.chat.controller.api.ChatResponse;
import backend.chat.controller.api.GuestCodeResponse;
import backend.chat.repository.MemberRepository;
import backend.chat.service.ChatService;
import backend.chat.service.Guest;
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
import java.util.Arrays;

@Tag(name = "채팅 API", description = "채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatService chatService;
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
    채팅 메시지를 AI 게스트에게 전달하고 응답을 반환합니다.
    케이스:
    1. 새로운 대화 시작: conversationId를 빈 값으로 전달 -> 서버에서 새로운 conversationId를 생성하여 반환합니다.
    2. 기존 대화 이어가기: 이전에 받은 conversationId를 그대로 요청에 포함시켜 대화를 이어갑니다.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전송 성공",
                    content = @Content(schema = @Schema(implementation = ChatResponse.class)))
    })
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        Guest guest = Guest.fromCode(request.getGuestCode());
        LocalDateTime chatSentTime = LocalDateTime.now();
        Member member = memberRepository.findById(defaultMemberId).orElseThrow(); // service로 감아야하는데 나중에
        Thread thread = chatService.findThread(member, request.getConversationId(), chatSentTime);
        String chatResponse = chatService.sendChat(thread.getConversationId(), request.getMessage(), guest);
        chatService.updateThread(member, thread, chatSentTime);
        return ResponseEntity.ok().body(new ChatResponse(chatResponse, chatSentTime, thread.getConversationId()));
    }

}