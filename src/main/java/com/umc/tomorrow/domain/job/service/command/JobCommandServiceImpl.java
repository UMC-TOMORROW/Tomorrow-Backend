package com.umc.tomorrow.domain.job.service.command;

import com.umc.tomorrow.domain.job.converter.JobConverter;
import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobCreateResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.entity.BusinessVerification;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.entity.PersonalRegistration;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.kakaoMap.service.KakaoMapService;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobCommandServiceImpl implements JobCommandService {

    private static final String JOB_SESSION_KEY = "job_session";

    private final JobConverter jobConverter;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final KakaoMapService kakaoMapService;

    // 일자리 폼 세션 저장
    @Override
    public JobStepResponseDTO saveInitialJobStep(Long userId, JobRequestDTO requestDTO, HttpSession session) {
        // 위도/경도 → 주소 변환 후 DTO 세팅
        String jobAddress = kakaoMapService.getAddressFromCoord(requestDTO.getLatitude(), requestDTO.getLongitude());
        requestDTO.setLocation(jobAddress);

        // 세션에 임시 저장
        session.setAttribute(JOB_SESSION_KEY, requestDTO);

        // 유저 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        return JobStepResponseDTO.builder()
                .registrantType(requestDTO.getRegistrantType())
                .step("job_form_saved")
                .build();
    }

    // 개인 등록 처리
    @Override
    public JobCreateResponseDTO savePersonalRegistration(Long userId, PersonalRequestDTO requestDTO,
                                                         HttpSession session) {
        User user = getUser(userId);
        JobRequestDTO jobDTO = getJobFromSession(session);

        // 개인 등록자인지 확인
        if (jobDTO.getRegistrantType() != RegistrantType.PERSONAL) {
            throw new RestApiException(JobErrorStatus.INVALID_REGISTRANT_TYPE);
        }

        // 주소 변환 후 DTO 세팅
        String personalAddress = kakaoMapService.getAddressFromCoord(requestDTO.getLatitude(), requestDTO.getLongitude());
        requestDTO.setAddress(personalAddress);

        PersonalRegistration personalRegistration = jobConverter.toPersonal(requestDTO);

        Job job = jobConverter.toJob(jobDTO).toBuilder()
                .user(user)
                .personalRegistration(personalRegistration)
                .build();

        // 연관관계 설정
        personalRegistration = personalRegistration.toBuilder()
                .job(job)
                .build();

        Job savedJob = jobRepository.save(job);
        session.removeAttribute(JOB_SESSION_KEY);

        return JobCreateResponseDTO.builder()
                .jobId(savedJob.getId())
                .build();
    }

    // 사업자 등록 여부 판단
    @Override
    public JobStepResponseDTO determineJobStep(Long userId, HttpSession session) {
        JobRequestDTO jobDTO = getJobFromSession(session);
        User user = getUser(userId);

        if (user.getBusinessVerification() != null) {
            // 사업자 인증 있음 → 잡 등록
            JobCreateResponseDTO jobResult = createJobWithExistingBusiness(userId, session);
            return JobStepResponseDTO.builder()
                    .step("job_created")
                    .jobId(jobResult.getJobId())
                    .registrantType(RegistrantType.BUSINESS)
                    .build();
        } else {
            // 인증 없음 → 사업자 등록 페이지 이동 step 반환
            return JobStepResponseDTO.builder()
                    .step("business-verifications/only")
                    .registrantType(RegistrantType.BUSINESS)
                    .build();
        }
    }

    // 기존 사업자 인증 있는 유저 잡 생성
    @Override
    public JobCreateResponseDTO createJobWithExistingBusiness(Long userId, HttpSession session) {
        User user = getUser(userId);

        if (user.getBusinessVerification() == null) {
            throw new RestApiException(GlobalErrorStatus._BAD_REQUEST);
        }

        //job세션 정보가 넘어왔는지
        JobRequestDTO jobDTO = getJobFromSession(session);

        // 등록자 유형 검증 (BUSINESS)
        validateRegistrantType(jobDTO);

        // 위도, 경도로 주소 저장
        String jobAddress = kakaoMapService.getAddressFromCoord(jobDTO.getLatitude(), jobDTO.getLongitude());
        jobDTO.setLocation(jobAddress);

        Job job = jobConverter.toJob(jobDTO).toBuilder()
                .user(user)
                .build();

        Job savedJob = jobRepository.save(job);
        session.removeAttribute(JOB_SESSION_KEY);

        return JobCreateResponseDTO.builder()
                .jobId(savedJob.getId())
                .build();
    }

    //헬퍼 메소드
    // 유저 조회
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
    }

    // 세션에서 JobRequestDTO 조회
    private JobRequestDTO getJobFromSession(HttpSession session) {
        JobRequestDTO dto = (JobRequestDTO) session.getAttribute(JOB_SESSION_KEY);
        if (dto == null) {
            throw new RestApiException(JobErrorStatus.JOB_DATA_NOT_FOUND);
        }
        return dto;
    }

    // 등록자 유형 검증
    private void validateRegistrantType(JobRequestDTO jobDTO) {
        if (jobDTO.getRegistrantType() != RegistrantType.BUSINESS) {
            throw new RestApiException(JobErrorStatus.INVALID_REGISTRANT_TYPE);
        }
    }

    /** ----------------- 마이페이지 사업자 정보 저장 ----------------- **/
    @Override
    public void saveBusinessVerification(Long userId, BusinessRequestDTO requestDTO) {
        User user = getUser(userId);
        BusinessVerification businessVerification = jobConverter.toBusiness(requestDTO);
        user.setBusinessVerification(businessVerification);
        userRepository.save(user);
    }

    /** ----------------- 모집 상태 변경 ----------------- **/
    @Transactional
    @Override
    public void updateJobStatus(Long userId, Long jobId, String status) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));

        if (!job.getUser().getId().equals(userId)) {
            throw new RestApiException(JobErrorStatus.JOB_FORBIDDEN);
        }

        PostStatus newStatus;
        try {
            newStatus = PostStatus.from(status);
        } catch (IllegalArgumentException e) {
            throw new RestApiException(JobErrorStatus.POST_STATUS_INVALID);
        }

        if (job.getStatus() == newStatus) {
            if (newStatus == PostStatus.OPEN) {
                throw new RestApiException(JobErrorStatus.JOB_ALREADY_OPEN);
            } else {
                throw new RestApiException(JobErrorStatus.JOB_ALREADY_CLOSED);
            }
        } else {
            job.updateStatus(newStatus);
        }
    }
}
