package com.umc.tomorrow.domain.jobbookmark.converter;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.jobbookmark.dto.response.JobBookmarkResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.entity.JobBookmark;

public class JobBookmarkConverter {
    public static JobBookmarkResponseDTO toJobBookmarkResponseDTO(JobBookmark jobBookmark) {
        Job job = jobBookmark.getJob();

        return JobBookmarkResponseDTO.builder()
                .id(jobBookmark.getId())
                .jobId(job.getId())
                .jobTitle(job.getTitle())
                .companyName(job.getCompanyName())
                .bookmarkedAt(String.valueOf(jobBookmark.getCreatedAt()))
                .build();
    }
}