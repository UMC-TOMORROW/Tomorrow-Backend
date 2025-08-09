/**
 * 커리어톡 채팅 메시지 저장 응답 DTO
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "채팅 메시지 저장 응답 DTO")
public class CreateChatMessageResponseDTO {

    @Schema(description = "저장된 메시지의 ID", example = "123")
    private final Long messageId;
}
