package com.umc.tomorrow.domain.job.service.command;

import com.umc.tomorrow.domain.job.converter.JobConverter;
import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationListResponse;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationResponse;
import com.umc.tomorrow.domain.job.dto.response.JobCreateResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.entity.BusinessVerification;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.entity.PersonalRegistration;
import com.umc.tomorrow.domain.job.entity.WorkEnvironment;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRecommendationJpaRepository;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.kakaoMap.service.KakaoMapService;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.preferences.entity.Preference;
import com.umc.tomorrow.domain.preferences.entity.PreferenceType;
import com.umc.tomorrow.domain.preferences.exception.PreferenceException;
import com.umc.tomorrow.domain.preferences.exception.code.PreferenceErrorStatus;
import com.umc.tomorrow.domain.preferences.repository.PreferenceRepository;
import com.umc.tomorrow.domain.review.repository.ReviewRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
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

    //일자리 정보 세션 임시 저장
    @Override
    public JobStepResponseDTO saveInitialJobStep(Long userId, JobRequestDTO requestDTO, HttpSession session) {
        String jobAddress = kakaoMapService.getAddressFromCoord(requestDTO.getLatitude(), requestDTO.getLongitude());
        requestDTO.setLocation(jobAddress);

        session.setAttribute(JOB_SESSION_KEY, requestDTO);

        userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        return JobStepResponseDTO.builder()
                .registrantType(requestDTO.getRegistrantType())
                .step("job_form_saved")
                .build();
    }

    //개인 사유 등록을 선택했을 경우
    @Override
    public JobCreateResponseDTO savePersonalRegistration(Long userId, PersonalRequestDTO requestDTO,
                                                         HttpSession session) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        JobRequestDTO jobDTO = (JobRequestDTO) session.getAttribute(JOB_SESSION_KEY);
        if (jobDTO == null) {
            throw new RestApiException(JobErrorStatus.JOB_DATA_NOT_FOUND);
        }

        // 등록자 유형 확인

        if (jobDTO.getRegistrantType() != RegistrantType.PERSONAL) {
            throw new RestApiException(JobErrorStatus.INVALID_REGISTRANT_TYPE);
        }

        String personalAddress = kakaoMapService.getAddressFromCoord(requestDTO.getLatitude(), requestDTO.getLongitude());
        requestDTO.setAddress(personalAddress);

        PersonalRegistration personalRegistration = jobConverter.toPersonal(requestDTO);

        Job job = jobConverter.toJob(jobDTO).toBuilder()
                .user(user)
                .personalRegistration(personalRegistration)
                .build();

        personalRegistration = personalRegistration.toBuilder()
                .job(job)
                .build();

        Job savedJob = jobRepository.save(job);
        session.removeAttribute(JOB_SESSION_KEY);

        return JobCreateResponseDTO.builder()
                .jobId(savedJob.getId())
                .build();
    }

    //기존에 사업자 정보가 등록되어 있는 사용자가 일자리를 등록할 경우
    @Override
    public JobCreateResponseDTO createJobWithExistingBusiness(Long userId, HttpSession session) {
        User user = getUser(userId);

        if (user.getBusinessVerification() == null) {
            throw new RestApiException(GlobalErrorStatus._BAD_REQUEST);
        }

        JobRequestDTO jobDTO = (JobRequestDTO) session.getAttribute(JOB_SESSION_KEY);
        if (jobDTO == null) {
            throw new RestApiException(JobErrorStatus.JOB_DATA_NOT_FOUND);
        }
//        JobRequestDTO jobDTO = getJobFromSession(session);

        // 등록자 유형 확인
        if (jobDTO.getRegistrantType() != RegistrantType.BUSINESS) {
            throw new RestApiException(JobErrorStatus.INVALID_REGISTRANT_TYPE);
        }

        //위도, 경도 기반 주소 조회 및 세팅
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

    //사업자 등록이 되지 않은 유저가 사업자 정보를 입력하고 동시에 일자리를 등록하는 경우
    @Override
    public JobCreateResponseDTO registerBusinessAndCreateJob(Long userId, BusinessRequestDTO requestDTO,
                                                             HttpSession session) {
        User user = getUser(userId);
//        JobRequestDTO jobDTO = getJobFromSession(session);
        JobRequestDTO jobDTO = (JobRequestDTO) session.getAttribute(JOB_SESSION_KEY);
        if (jobDTO == null) {
            throw new RestApiException(JobErrorStatus.JOB_DATA_NOT_FOUND);
        }

        // 등록자 유형 확인
        if (jobDTO.getRegistrantType() != RegistrantType.BUSINESS) {
            throw new RestApiException(JobErrorStatus.INVALID_REGISTRANT_TYPE);
        }

        //위도, 경도 기반 주소 조회 및 세팅
        String jobAddress = kakaoMapService.getAddressFromCoord(jobDTO.getLatitude(), jobDTO.getLongitude());
        jobDTO.setLocation(jobAddress);

        BusinessVerification businessVerification = jobConverter.toBusiness(requestDTO);
        user.setBusinessVerification(businessVerification);
        userRepository.save(user);

        Job job = jobConverter.toJob(jobDTO).toBuilder()
                .user(user)
                .build();

        Job savedJob = jobRepository.save(job);
        session.removeAttribute(JOB_SESSION_KEY);

        return JobCreateResponseDTO.builder()
                .jobId(savedJob.getId())
                .build();
    }

    //권한 검증
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
    }

    private JobRequestDTO getJobFromSession(HttpSession session) {
        JobRequestDTO dto = (JobRequestDTO) session.getAttribute(JOB_SESSION_KEY);
        if (dto == null) {
            throw new RestApiException(GlobalErrorStatus._BAD_REQUEST);
        }
        return dto;
    }

    //마이페이지에서 사업자 정보만 저장(일자리 등록x)
    @Override
    public void saveBusinessVerification(Long userId, BusinessRequestDTO requestDTO) {
        User user = getUser(userId);

        BusinessVerification businessVerification = jobConverter.toBusiness(requestDTO);
        user.setBusinessVerification(businessVerification);
        userRepository.save(user);
    }

    // PATCH 공고 모집완료/모집전 처리하기
    @Transactional
    @Override
    public void updateJobStatus(Long userId, Long jobId, String status) {
        // 1. jobId에 해당하는 공고가 존재하는지 확인
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));

        // 2. 권한 검증: 공고 등록자와 현재 요청한 사용자가 동일한지 확인
        if (!job.getUser().getId().equals(userId)) {
            // JOB_FORBIDDEN 예외로 변경
            throw new RestApiException(JobErrorStatus.JOB_FORBIDDEN);
        }

        // 3. 입력받은 status 문자열을 PostStatus Enum으로 변환
        PostStatus newStatus;
        try {
            newStatus = PostStatus.from(status);
        } catch (IllegalArgumentException e) {
            // POST_STATUS_INVALID 예외로 변경
            throw new RestApiException(JobErrorStatus.POST_STATUS_INVALID);
        }

        // 4. 비즈니스 로직에 따라 상태 변경
        if (job.getStatus() == newStatus) {
            // 현재 상태와 변경하려는 상태가 동일한 경우
            if (newStatus == PostStatus.OPEN) {
                throw new RestApiException(JobErrorStatus.JOB_ALREADY_OPEN); // 아직 마감 안된 공고입니다.
            } else { // newStatus == PostStatus.CLOSED
                throw new RestApiException(JobErrorStatus.JOB_ALREADY_CLOSED); // 이미 마감된 공고입니다.
            }
        } else {
            // 현재 상태와 변경하려는 상태가 다른 경우 (CLOSED -> OPEN 또는 OPEN -> CLOSED)
            job.updateStatus(newStatus);
        }
    }
}
