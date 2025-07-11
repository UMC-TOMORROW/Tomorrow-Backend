package com.umc.tomorrow.domain.job.service.command;

import com.umc.tomorrow.domain.job.dto.request.CreateJobRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class JobCommandService {

    private static final String JOB_SESSION_KEY = "job_session";

    public JobStepResponseDTO saveInitialJobStep(CreateJobRequestDTO requestDTO, HttpSession session) {

        session.setAttribute(JOB_SESSION_KEY, requestDTO);

        return JobStepResponseDTO.builder()
                .registrantType(requestDTO.getRegistrantType())
                .step("job_form_saved")
                .build();
    }
}
