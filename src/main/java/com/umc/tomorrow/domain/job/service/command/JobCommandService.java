package com.umc.tomorrow.domain.job.service.command;

import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import jakarta.servlet.http.HttpSession;

public interface JobCommandService {

    JobStepResponseDTO saveInitialJobStep(Long userId,JobRequestDTO requestDTO, HttpSession session);

    void saveBusinessVerification(Long userId, BusinessRequestDTO requestDTO);

    void savePersonalRegistration(Long userId, PersonalRequestDTO requestDTO);
}
