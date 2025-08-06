/**
 * 채팅 메시지 조회 목록 응답 DTO(무한 스크롤)
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "채팅 메시지 조회 목록 응답 DTO(무한 스크롤)")
public class GetChatMessageListResponseDTO {

    @Schema(description = "채팅 메시지 목록")
    private List<GetChatMessageResponseDTO> chatMessageList;

    @Schema(description = "다음 메시지가 있는지 여부")
    private boolean hasNext;
}
