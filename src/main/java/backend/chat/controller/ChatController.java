package backend.chat.controller;

import backend.chat.service.ChatService;
import backend.chat.service.Guest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/api/chat")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        Guest guest = Guest.fromCode(request.guestCode());
        String response = chatService.sendChat("1234",request.message(), guest);
        return ResponseEntity.ok(new ChatResponse(response));
    }

}
