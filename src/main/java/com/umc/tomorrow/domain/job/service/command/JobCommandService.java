package com.umc.tomorrow.domain.job.service.command;

import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.CreateJobRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.entity.Business;
import com.umc.tomorrow.domain.job.entity.PersonalRegistration;
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

    public void saveBusinessVerification(BusinessRequestDTO requestDTO) {

        Business business = new Business();
        business.setBizNumber(requestDTO.getBizNumber());
        business.setCompanyName(requestDTO.getCompanyName());
        business.setOwnerName(requestDTO.getOwnerName());
        business.setOpeningDate(requestDTO.getOpeningDate());
    }

    public void savePersonalRegistration(PersonalRequestDTO requestDTO) {

        PersonalRegistration personalRegistration = new PersonalRegistration();
        personalRegistration.setName(requestDTO.getName());
        personalRegistration.setLatitude(requestDTO.getLatitude());
        personalRegistration.setLongitude(requestDTO.getLongitude());
        personalRegistration.setContact(requestDTO.getContact());
        personalRegistration.setRegistrationPurpose(requestDTO.getRegistrationPurpose());
        personalRegistration.setAddress(requestDTO.getAddress());
    }
}
