package backend.chat.controller.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "채팅 요청 DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @Schema(
            description = "대화 식별자 (빈 문자열이거나 UUID 형식)",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @Pattern(
            regexp = "|[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}",
            message = "conversationId는 빈 문자열이거나 UUID 형식이어야 합니다."
    )
    private String conversationId;

    @Schema(description = "사용자가 보낸 메시지 (최대 200자)", example = "나 오늘 행복하지가 않아")
    @NotBlank(message = "message는 비어 있을 수 없습니다.")
    @Size(max = 200, message = "메시지는 최대 200자까지 가능합니다.")
    private String message;

    @Schema(description = "게스트 코드", example = "ybj")
    private String guestCode;
}