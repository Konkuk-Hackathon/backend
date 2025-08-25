package backend.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Test API", description = "테스트용 API 입니다.")
public class TestController {

    @Operation(summary = "테스트 API 호출", description = "단순히 테스트 문자열을 반환합니다.")
    @GetMapping("/api/v1/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("테스트 API 입니다.");
    }
}
