package com.umc.tomorrow.domain.job.service.command;

import com.umc.tomorrow.domain.job.converter.JobConverter;
import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.entity.BusinessVerification;
import com.umc.tomorrow.domain.job.entity.PersonalRegistration;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobCommandServiceImpl implements JobCommandService {

    private static final String JOB_SESSION_KEY = "job_session";
    private final JobConverter jobConverter;
    private final UserRepository userRepository;

    @Override
    public JobStepResponseDTO saveInitialJobStep(Long userId, JobRequestDTO requestDTO, HttpSession session) {
        session.setAttribute(JOB_SESSION_KEY, requestDTO);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        return JobStepResponseDTO.builder()
                .registrantType(requestDTO.getRegistrantType())
                .step("job_form_saved")
                .user(user)
                .build();
    }

    @Override
    public void saveBusinessVerification(Long userId, BusinessRequestDTO requestDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        BusinessVerification businessVerification = jobConverter.toBusiness(requestDTO);
    }

    @Override
    public void savePersonalRegistration(Long userId, PersonalRequestDTO requestDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        PersonalRegistration personalRegistration = jobConverter.toPersonal(requestDTO);
    }
}
