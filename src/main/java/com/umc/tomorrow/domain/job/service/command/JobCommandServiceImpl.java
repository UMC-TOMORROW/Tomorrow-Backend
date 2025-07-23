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
    public JobStepResponseDTO saveInitialJobStep(String username, JobRequestDTO requestDTO, HttpSession session) {
        session.setAttribute(JOB_SESSION_KEY, requestDTO);

        User user = userRepository.findByUsername(username);

        return JobStepResponseDTO.builder()
                .registrantType(requestDTO.getRegistrantType())
                .step("job_form_saved")
                .user(user)
                .build();
    }

    @Override
    public void saveBusinessVerification(BusinessRequestDTO requestDTO) {
        BusinessVerification businessVerification = jobConverter.toBusiness(requestDTO);
    }

    @Override
    public void savePersonalRegistration(PersonalRequestDTO requestDTO) {
        PersonalRegistration personalRegistration = jobConverter.toPersonal(requestDTO);
    }
}
