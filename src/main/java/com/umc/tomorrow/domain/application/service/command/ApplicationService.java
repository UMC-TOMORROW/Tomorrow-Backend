/**
 * 지원서 서비스
 * - 지원서 관련 비즈니스 로직 처리
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.service.command;

import com.umc.tomorrow.domain.application.converter.ApplicationConverter;
import com.umc.tomorrow.domain.application.dto.request.CreateApplicationRequestDTO;
import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicantListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.CreateApplicationResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationStatusListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationDetailsResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import com.umc.tomorrow.domain.application.exception.code.ApplicationErrorStatus;
import com.umc.tomorrow.domain.application.repository.ApplicationRepository;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.exception.ResumeException;
import com.umc.tomorrow.domain.resume.exception.code.ResumeErrorStatus;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    /**
     * 지원서 상태 업데이트 (합격/불합격 처리)
     */
    @Transactional
    public UpdateApplicationStatusResponseDTO updateApplicationStatus(
            Long postId,
            Long applicationId,
            UpdateApplicationStatusRequestDTO requestDTO
    ) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RestApiException(ApplicationErrorStatus.APPLICATION_NOT_FOUND));

        Job job = jobRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));

        if (!application.getJob().getId().equals(postId)) {
            throw new RestApiException(ApplicationErrorStatus.APPLICATION_JOB_MISMATCH);
        }

        ApplicationStatus status = ApplicationConverter.toEnum(requestDTO);
        application.updateStatus(status);
        applicationRepository.save(application);

        return UpdateApplicationStatusResponseDTO.builder()
                .applicationId(applicationId)
                .status(requestDTO.getStatus())
                .build();
    }

    /**
     * 일자리에 지원하기
     */
    @Transactional
    public CreateApplicationResponseDTO createApplication(Long userId, CreateApplicationRequestDTO requestDTO) {
        Job job = jobRepository.findById(requestDTO.getJobId())
                .orElseThrow(() -> new JobException(JobErrorStatus.JOB_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        Application application;

        if (requestDTO.getResumeId() != null) {
            Resume resume = resumeRepository.findByIdAndUserId(requestDTO.getResumeId(), userId)
                    .orElseThrow(() -> new ResumeException(ResumeErrorStatus.RESUME_NOT_FOUND));

            application = Application.builder()
                    .content(requestDTO.getContent())
                    .job(job)
                    .user(user)
                    .resume(resume)
                    .appliedAt(LocalDateTime.now())
                    .build();
        } else {
            application = Application.builder()
                    .content(requestDTO.getContent())
                    .job(job)
                    .user(user)
                    .appliedAt(LocalDateTime.now())
                    .build();
        }

        applicationRepository.save(application);

        User jobOwner = job.getUser();
        String ownerEmail = jobOwner.getEmail();

        // 이메일 보내는 서비스 로직 추가해야함

        return CreateApplicationResponseDTO.builder()
                .id(application.getId())
                .build();
    }

    /*
     * 개별 지원자 이력서 조회
     */
    public ApplicationDetailsResponseDTO getApplicantResume(Long postId, Long applicantId) {
        Application application = applicationRepository.findByJobIdAndUserId(postId, applicantId)
                .orElseThrow(() -> new RestApiException(ApplicationErrorStatus.APPLICATION_NOT_FOUND));

        User user = application.getUser();
        Resume resume = application.getResume();

        return ApplicationConverter.toApplicantResumeResponseDTO(
                application,
                user,
                resume
        );
    }

    /**
     * 공고 기준 지원자 목록 조회
     * status : null이면 전체, open이면 모집중, closed면 모집완료
     */
    private boolean isJobClosed(Job job) {
        LocalDateTime now = LocalDateTime.now();
        boolean deadlinePassed = job.getDeadline().isBefore(now);
        boolean manuallyClosed = Boolean.FALSE.equals(job.getIsActive());
        return deadlinePassed || manuallyClosed;
    }

    @Transactional(readOnly = true)
    public List<ApplicantListResponseDTO> getApplicantsByPostAndStatus(Long postId, String status) {
        Job job = jobRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));

        boolean isClosed = isJobClosed(job);

        List<Application> applications;
        if (status == null || status.isBlank()) {
            applications = applicationRepository.findAllByJobId(postId);
        } else if (status.equalsIgnoreCase("open")) {
            applications = isClosed ? List.of() : applicationRepository.findAllByJobId(postId);
        } else if (status.equalsIgnoreCase("closed")) {
            applications = isClosed ? applicationRepository.findAllByJobId(postId) : List.of();
        } else {
            throw new RestApiException(ApplicationErrorStatus.INVALID_STATUS);
        }

        return applications.stream()
                .map(ApplicationConverter::toApplicantListResponseDTO)
                .collect(Collectors.toList());
    }

}
