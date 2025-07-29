package com.umc.tomorrow.domain.searchAndFilter.controller;

import com.umc.tomorrow.domain.searchAndFilter.dto.request.JobSearchRequestDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobSearchResponseDTO;
import com.umc.tomorrow.domain.searchAndFilter.service.JobSearchService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SearchAndFilter", description = "검색 기능 관련 API")
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobSearchController {

    private final JobSearchService jobSearchService;

    /**
     * 일자리 정보 세션에 저장(POST)
     * @param requestDTO 검색 조건 DTO
     * @return 성공 응답
     */
    @PostMapping("/search")
    public ResponseEntity<List<JobSearchResponseDTO>> searchJobs(@RequestBody JobSearchRequestDTO requestDTO) {
        List<JobSearchResponseDTO> result = jobSearchService.searchJobs(requestDTO);
       // return ResponseEntity.ok(result);
        return ResponseEntity.ok(BaseResponse.onSuccess(result).getResult());
    }
}
