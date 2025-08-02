package com.umc.tomorrow.domain.searchAndFilter.converter;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobSearchResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class JobSearchConverter {

    public JobSearchResponseDTO toResponseDTO(Job job) {
        return JobSearchResponseDTO.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .location(job.getLocation())
                .companyName(job.getCompanyName())
                .salary(job.getSalary())
                .workStart(job.getWorkStart())
                .workEnd(job.getWorkEnd())
                .jobCategory(job.getJobCategory())
                .build();
    }
}
