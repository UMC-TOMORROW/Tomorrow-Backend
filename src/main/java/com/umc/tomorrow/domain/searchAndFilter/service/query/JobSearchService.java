package com.umc.tomorrow.domain.searchAndFilter.service.query;

import com.umc.tomorrow.domain.searchAndFilter.dto.request.JobSearchRequestDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobResponseDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobSearchResponseDTO;

import java.util.List;

public interface JobSearchService {
    JobResponseDTO searchJobs(JobSearchRequestDTO requestDTO);
    JobResponseDTO getAllActiveJobs();
}