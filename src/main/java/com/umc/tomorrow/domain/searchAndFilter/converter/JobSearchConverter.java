package com.umc.tomorrow.domain.searchAndFilter.converter;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.entity.WorkDays;
import com.umc.tomorrow.domain.job.entity.WorkEnvironment;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.JobSearchResponseDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.WorkDaysDTO;
import com.umc.tomorrow.domain.searchAndFilter.dto.response.WorkEnvironmentDTO;
import org.springframework.stereotype.Component;

@Component
public class JobSearchConverter {

    public JobSearchResponseDTO toResponseDTO(Job job) {

        // WorkDays -> WorkDaysDTO 변환
        WorkDays workDays = job.getWorkDays();
        WorkDaysDTO workDaysDTO = null;

        if (workDays != null) {
            workDaysDTO = WorkDaysDTO.builder()
                    .mon(workDays.getMON())
                    .tue(workDays.getTUE())
                    .wed(workDays.getWED())
                    .thu(workDays.getTHU())
                    .fri(workDays.getFRI())
                    .sat(workDays.getSAT())
                    .sun(workDays.getSUN())
                    .isDayNegotiable(workDays.getIsDayNegotiable())
                    .build();
        }

        // WorkEnvironment -> WorkEnvironmentDTO 변환
        WorkEnvironment workEnv = job.getWorkEnvironment();
        WorkEnvironmentDTO workEnvironmentDTO = null;

        if (workEnv != null) {
            workEnvironmentDTO = WorkEnvironmentDTO.builder()
                    .canWorkSitting(workEnv.isCanWorkSitting())
                    .canWorkStanding(workEnv.isCanWorkStanding())
                    .canCarryObjects(workEnv.isCanCarryObjects())
                    .canMoveActively(workEnv.isCanMoveActively())
                    .canCommunicate(workEnv.isCanCommunicate())
                    .build();
        }

        return JobSearchResponseDTO.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .location(job.getLocation())
                .companyName(job.getCompanyName())
                .salary(job.getSalary())
                .workStart(job.getWorkStart())
                .workEnd(job.getWorkEnd())
                .jobCategory(job.getJobCategory())
                .workDays(workDaysDTO)
                .jobImageUrl(job.getJobImageUrl())
                .paymentType(job.getPaymentType())
                .workEnvironment(workEnvironmentDTO)
                .workPeriod(job.getWorkPeriod())
                .build();
    }
}
