/**
 * 커리어톡 채팅방 메시지 생성 요청 DTO
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "커리어톡 채팅방 메시지 생성 요청 DTO")
public class CreateChatMessageRequestDTO {

    @Schema(
            description = "커리어톡 채팅방 id",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull @Positive
    private Long chattingRoomId;

    @Schema(
            description = "메시지 내용",
            example = "요양 간호사가 될 수 있는 꿀팁 알려드립니다.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank @Size(max = 255, message = "careertalk.chatting.content")
    private String content;

    @Schema(description = "메시지 전송 시각 (ISO-8601)", example = "2025-08-06T10:12:34")
    @NotNull
    private LocalDateTime sentAt;
}
