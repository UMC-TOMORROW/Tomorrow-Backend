/**
 * JobBookmarkCommandServiceImpl
 * -JobBookmarkCommandService의 구현체입니다.
    작성자 : 정여진
    작성일 : 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.service.command;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.jobbookmark.dto.response.JobBookmarkResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.entity.JobBookmark;
import com.umc.tomorrow.domain.jobbookmark.exception.code.JobBookmarkErrorStatus;
import com.umc.tomorrow.domain.jobbookmark.repository.JobBookmarkRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.exception.MemberStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import jakarta.transaction.Transactional;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobBookmarkCommandServiceImpl implements JobBookmarkCommandService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobBookmarkRepository jobBookmarkRepository;

    /**
     * 특정 사용자가 특정 직무를 찜하는
     *
     * @param userId 북마크를 생성하는 사용자의 ID
     * @param jobId 북마크할 직무의 ID
     * @return 생성된 북마크 정보를 담은 응답 DTO
     */
    @Override
    @Transactional
    public JobBookmarkResponseDTO save(Long userId, Long jobId) {
        // 1. User와 Job 엔티티를 데이터베이스에서 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(MemberStatus.USER_NOT_FOUND));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));

        // 2. JobBookmark 엔티티를 생성하고 연관 관계
        JobBookmark jobBookmark = JobBookmark.builder()
                .user(user)
                .job(job)
                .build();
        // 3.생성된 엔티티를 데이터베이스에 저장
        jobBookmarkRepository.save(jobBookmark);

        // 4. return
        return JobBookmarkResponseDTO.builder()
                .id(jobBookmark.getId())
                .jobId(jobId)
                .jobTitle(job.getTitle())
                .companyName(job.getCompanyName())
                .bookmarkedAt(jobBookmark.getCreatedAt().toString()) // LocalDateTime -> String으로
                .build();
    }

    @Override
    @Transactional
    public void delete(Long userId, Long jobId) {
        // Find and delete the bookmark
        JobBookmark jobBookmark = jobBookmarkRepository.findByUserIdAndJobId(userId, jobId)
                .orElseThrow(() -> new RestApiException(JobBookmarkErrorStatus.JOB_BOOKMARK_NOT_FOUND));

        jobBookmarkRepository.delete(jobBookmark);
    }
}