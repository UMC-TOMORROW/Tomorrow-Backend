/**
 * 내 공고 조회 서비스 구현체
 * 작성자: 정인도
 * 생성일: 2025-07-25
 */
package com.umc.tomorrow.domain.job.service.query;

import com.umc.tomorrow.domain.job.converter.JobConverter;
import com.umc.tomorrow.domain.job.dto.request.MyPostResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.JobDetailResponseDTO;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.job.service.query.JobQueryService;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobQueryServiceImpl implements JobQueryService {

    private final JobRepository jobRepository;
    private final JobConverter jobConverter;

    @Override
    public List<MyPostResponseDTO> getMyPosts(Long userId, String status) {
        PostStatus postStatus;

        try {
            postStatus = PostStatus.from(status);
        } catch (IllegalArgumentException e) {
            throw new JobException(JobErrorStatus.POST_STATUS_INVALID);
        }

        List<Job> jobs = jobRepository.findByUserIdAndStatus(userId, postStatus);

        return jobs.stream()
                .map(jobConverter::toMyPostResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobDetailResponseDTO getJobDetail(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));

        return jobConverter.toJobDetailResponseDTO(job);
    }

}
