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

import com.umc.tomorrow.domain.application.dto.response.ApplicationDetailsResponseDTO;


import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import com.umc.tomorrow.domain.application.exception.code.ApplicationErrorStatus;
import com.umc.tomorrow.domain.application.exception.ApplicationException;
import com.umc.tomorrow.domain.application.repository.ApplicationRepository;
import com.umc.tomorrow.domain.email.dto.request.EmailRequestDTO;
import com.umc.tomorrow.domain.email.enums.EmailType;
import com.umc.tomorrow.domain.email.service.EmailService;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.exception.MemberException;
import com.umc.tomorrow.domain.member.exception.code.MemberStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.exception.ResumeException;
import com.umc.tomorrow.domain.resume.exception.code.ResumeErrorStatus;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationCommandService {

    private final EmailService emailService;
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
        // 지원서 조회
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RestApiException(ApplicationErrorStatus.APPLICATION_NOT_FOUND));

        // 공고 조회 및 검증
        Job job = jobRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));
        
        // 지원서가 해당 공고에 대한 것인지 검증
        if (!application.getJob().getId().equals(postId)) {
            throw new RestApiException(ApplicationErrorStatus.APPLICATION_JOB_MISMATCH);
        }

        ApplicationStatus status =  ApplicationConverter.toEnum(requestDTO);

        application.updateStatus(status);
        applicationRepository.save(application);
        
        // 지원 결과에 따른 메일 발송
        EmailType emailType = status == ApplicationStatus.ACCEPTED ? EmailType.JOB_ACCEPTED : EmailType.JOB_REJECTED;
        EmailRequestDTO emailRequestDTO = EmailRequestDTO.builder()
                .jobId(job.getId())
                .type(emailType)
                .build();
        
        emailService.sendEmail(application.getUser().getId(), emailRequestDTO);
        
        return UpdateApplicationStatusResponseDTO.builder()
                .applicationId(applicationId)
                .status(requestDTO.getStatus())
                .build();
    }

    /**
     * 일자리에 지원하기
     *
     * @param userId     일자리에 지원하는 userId
     * @param requestDTO 일자리 지원 요청 DTO
     * @return 일자리 응답 DTO
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
                    .status(ApplicationStatus.REJECTED)
                    .appliedAt(LocalDateTime.now())
                    .build();
        } else {
            application = Application.builder()
                    .content(requestDTO.getContent())
                    .job(job)
                    .user(user)
                    .status(ApplicationStatus.REJECTED)
                    .appliedAt(LocalDateTime.now())
                    .build();
        }

        applicationRepository.save(application);

        EmailRequestDTO emailRequestDTO = EmailRequestDTO.builder()
                .jobId(job.getId())
                .type(EmailType.JOB_APPLY)
                .build();

        emailService.sendEmail(userId, emailRequestDTO);

        return CreateApplicationResponseDTO.builder()
                .id(application.getId())
                .build();
    }

    /*
     * 개별 지원자 이력서 조회
     */
    public ApplicationDetailsResponseDTO getApplicantResume(Long postId, Long applicantId) {
        Application application = applicationRepository.findByJobIdAndUserIdWithResume(postId, applicantId)
                .orElseThrow(() -> new ApplicationException(ApplicationErrorStatus.APPLICATION_NOT_FOUND));
        User user = application.getUser();
        // Resume 기본 정보만 가져오기 (Introduction은 별도 로드)
        Resume resume = resumeRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElse(null);
        
        // Resume이 있고 Introduction이 없다면 Introduction 로드
        if (resume != null && resume.getIntroduction() == null) {
            // Introduction을 별도로 로드하는 로직이 필요할 수 있음
            // 현재는 기본 Resume 정보만 사용
        }
        
        return ApplicationConverter.toApplicantResumeResponseDTO(
                application,
                user,
                resume
        );
    }


    /**
     * 공고 기준 지원자 목록 조회
     * status : null이면 전체, open이면 모집중, closed면 모집완료
     * */
    private boolean isJobClosed(Job job) {
        LocalDateTime now = LocalDateTime.now();
        boolean deadlinePassed = job.getDeadline().isBefore(now);
        boolean manuallyClosed = job.getStatus() == PostStatus.CLOSED;
        return deadlinePassed || manuallyClosed;
    }

    @Transactional(readOnly = true)
    public List<ApplicantListResponseDTO> getApplicantsByPostAndStatus(Long postId, String status) {
        try {
            Job job = jobRepository.findById(postId)
                    .orElseThrow(() -> new ApplicationException(ApplicationErrorStatus.JOB_NOT_FOUND));

            boolean isClosed = isJobClosed(job);

            List<Application> applications;
            if (status == null || status.isBlank()) {
                // 전체
                applications = applicationRepository.findAllByJobId(postId);
            } else if (status.equalsIgnoreCase("open")) {
                applications = isClosed ? List.of() : applicationRepository.findAllByJobId(postId);
            } else if (status.equalsIgnoreCase("closed")) {
                applications = isClosed ? applicationRepository.findAllByJobId(postId) : List.of();
            } else {
                throw new ApplicationException(ApplicationErrorStatus.INVALID_STATUS);
            }

            // 지원자가 없어도 빈 리스트 반환 (404 에러 아님)
            return applications.stream()
                    .map(ApplicationConverter::toApplicantListResponseDTO)
                    .collect(Collectors.toList());
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            // 예상치 못한 예외 발생 시 로그 기록 후 적절한 에러 응답
            throw new ApplicationException(ApplicationErrorStatus.APPLICANTS_NOT_FOUND);
        }
    }

}