package com.umc.tomorrow.domain.searchAndFilter.service;

import com.umc.tomorrow.domain.searchAndFilter.dto.request.JobSearchRequestDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobSearchResponseDTO;

import java.util.List;

public interface JobSearchService {
    List<JobSearchResponseDTO> searchJobs(JobSearchRequestDTO requestDTO);
}