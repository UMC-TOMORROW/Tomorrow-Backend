package com.umc.tomorrow.domain.job.service.command;

import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import jakarta.servlet.http.HttpSession;

public interface JobCommandService {

    JobStepResponseDTO saveInitialJobStep(String username,JobRequestDTO requestDTO, HttpSession session);

    void saveBusinessVerification(BusinessRequestDTO requestDTO);

    void savePersonalRegistration(PersonalRequestDTO requestDTO);
}
