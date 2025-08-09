package com.umc.tomorrow.domain.job.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내일 추천 게시물 리스트 DTO")
public class GetRecommendationListResponse {

    @Schema(description = "내일 추천 게시글 목록")
    private List<GetRecommendationResponse> recommendationList;

    @Schema(description = "다음 게시물이 있는지 여부")
    private boolean hasNext;
}
