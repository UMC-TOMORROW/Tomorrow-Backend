package com.umc.tomorrow.domain.careertalk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "커리어톡 게시글 목록 조회 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GetCareertalkListResponseDto {

    @Schema(description = "게시글 목록")
    private List<GetCareertalkResponseDto> careertalkList;

    @Schema(description = "다음 게시물이 있는지 여부")
    private boolean hasNext;
}
