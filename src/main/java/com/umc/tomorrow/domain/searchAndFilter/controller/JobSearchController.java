package com.umc.tomorrow.domain.searchAndFilter.controller;

import com.umc.tomorrow.domain.searchAndFilter.dto.request.JobSearchRequestDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobResponseDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobSearchResponseDTO;
import com.umc.tomorrow.domain.searchAndFilter.service.query.JobSearchService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SearchAndFilter", description = "검색 기능 관련 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JobSearchController {

    private final JobSearchService jobSearchService;

    /**
     * 일자리 검색(POST)
     * @param requestDTO 검색 조건 DTO
     * @return 성공 응답
     */
    @PostMapping("/jobs/search")
    public ResponseEntity<JobResponseDTO> searchJobs(@RequestBody JobSearchRequestDTO requestDTO) {
        JobResponseDTO result = jobSearchService.searchJobs(requestDTO);
        return ResponseEntity.ok(BaseResponse.onSuccess(result).getResult());
    }

    /**
     * 일자리 목록 조회(GET)
     * @return 성공 응답
     */
    @Operation(summary = "일자리 조회 (GET)", description = "검색 조건을 쿼리스트링으로 받아 일자리를 조회합니다.")
    @GetMapping("/jobsView")
    public ResponseEntity<JobResponseDTO> getAllActiveJobs() {
        JobResponseDTO result = jobSearchService.getAllActiveJobs();
        return ResponseEntity.ok(BaseResponse.onSuccess(result).getResult());
    }


}
