/**
 * 채팅 메시지 조회 응답 DTO(무한 스크롤)
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "채팅 메시지 조회 응답 DTO")
public class GetChatMessageResponseDTO {

    @Schema(description = "메시지 id")
    private Long messageId;

    @Schema(description = "익명이름")
    private String anonymousName;

    @Schema(description = "메시지 내용")
    private String content;

    @Schema(description = "메시지 작성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "메시지 본인 여부")
    private boolean isMine;

}
