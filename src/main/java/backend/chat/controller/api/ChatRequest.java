package backend.chat.controller.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "채팅 요청 DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @Schema(description = "사용자가 보낸 메시지 (최대 200자)", example = "나 오늘 행복하지가 않아")
    @NotBlank(message = "message는 비어 있을 수 없습니다.")
    @Size(max = 200, message = "메시지는 최대 200자까지 가능합니다.")
    private String message;

    @Schema(description = "게스트 코드", example = "ybj")
    private String guestCode;
}