package com.umc.tomorrow.domain.searchAndFilter.dto.response;

import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class JobSearchResponseWrapper {

    @Schema(description = "검색되는 일자리 수", example = "5")
    private int jobCount;

    @Schema(description = "검색된 일자리 목록")
    private List<JobResponseDTO> jobs;
}
