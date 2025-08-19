package com.umc.tomorrow.domain.searchAndFilter.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class JobResponseDTO {
    private int jobCount; // 전체 일자리 수
    private List<JobSearchResponseDTO> jobs; // 일자리 목록
}