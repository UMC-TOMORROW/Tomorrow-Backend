package com.umc.tomorrow.domain.searchAndFilter.repository;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.searchAndFilter.dto.request.JobSearchRequestDTO;

import java.util.List;

public interface JobSearchRepository {
    List<Job> searchJobs(JobSearchRequestDTO dto);
}

