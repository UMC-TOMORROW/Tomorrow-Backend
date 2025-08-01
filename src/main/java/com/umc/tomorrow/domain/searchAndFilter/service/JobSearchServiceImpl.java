package com.umc.tomorrow.domain.searchAndFilter.service;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.searchAndFilter.converter.JobSearchConverter;
import com.umc.tomorrow.domain.searchAndFilter.dto.request.JobSearchRequestDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobSearchResponseDTO;
import com.umc.tomorrow.domain.searchAndFilter.repository.JobSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * - 전달받은 검색 조건에 따라 일자리 목록을 조회함
 * - 조회된 Job 엔티티를 JobSearchResponseDTO로 변환하여 반환
 */
@Service
@RequiredArgsConstructor
public class JobSearchServiceImpl implements JobSearchService {

    // 일자리 검색 쿼리를 처리하는 Repository
    private final JobSearchRepository jobSearchRepository;

    // Job 엔티티를 JobSearchResponseDTO로 변환하는 Converter
    private final JobSearchConverter converter;

    /**
     * 검색 조건을 기반으로 일자리 목록을 조회하고 DTO로 변환하여 반환
     * @param requestDTO 검색 조건이 담긴 DTO
     * @return 조건에 맞는 일자리 목록의 응답 DTO 리스트
     */
    @Override
    public List<JobSearchResponseDTO> searchJobs(JobSearchRequestDTO requestDTO) {
        // 조건에 맞는 Job 엔티티 목록 조회
        List<Job> jobs = jobSearchRepository.searchJobs(requestDTO);

        // Job → JobSearchResponseDTO 변환 후 리스트로 반환
        return jobs.stream()
                .map(converter::toResponseDTO)
                .collect(Collectors.toList());
    }
}
