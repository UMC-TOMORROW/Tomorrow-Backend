package com.umc.tomorrow.domain.job.converter;

import com.umc.tomorrow.domain.job.dto.request.*;
import com.umc.tomorrow.domain.job.entity.*;
import org.springframework.stereotype.Component;

@Component
public class JobConverter {

    public Job toJob(JobRequestDTO dto) {
        return Job.builder()
                .title(dto.getTitle())
                .workPeriod(dto.getWorkPeriod())
                .isPeriodNegotiable(dto.getIsPeriodNegotiable())
                .workStart(dto.getWorkStart())
                .workEnd(dto.getWorkEnd())
                .isTimeNegotiable(dto.getIsTimeNegotiable())
                .paymentType(dto.getPaymentType())
                .salary(dto.getSalary())
                .jobDescription(dto.getJobDescription())
                .jobImageUrl(dto.getJobImageUrl())
                .companyName(dto.getCompanyName())
                .isActive(dto.getIsActive())
                .recruitmentLimit(dto.getRecruitmentLimit())
                .registrantType(dto.getRegistrantType())
                .deadline(dto.getDeadline())
                .preferredQualifications(dto.getPreferredQualifications())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .location(dto.getLocation())
                .alwaysHiring(dto.getAlwaysHiring())
                .workDays(toWorkDays(dto.getWorkDays()))
                .workEnvironment(toWorkEnvironment(dto.getWorkEnvironment()))
                .build();
    }

    public WorkDays toWorkDays(WorkDaysRequestDTO dto) {
        return WorkDays.builder()
                .MON(dto.getMON())
                .TUE(dto.getTUE())
                .WED(dto.getWED())
                .THU(dto.getTHU())
                .FRI(dto.getFRI())
                .SAT(dto.getSAT())
                .SUN(dto.getSUN())
                .isDayNegotiable(dto.getIsDayNegotiable())
                .build();
    }

    public WorkEnvironment toWorkEnvironment(WorkEnvironmentRequestDTO dto) {
        return WorkEnvironment.builder()
                .canWorkSitting(dto.getCanWorkSitting())
                .canWorkStanding(dto.getCanWorkStanding())
                .canLiftHeavyObjects(dto.getCanLiftHeavyObjects())
                .canLiftLightObjects(dto.getCanLiftLightObjects())
                .canMoveActively(dto.getCanMoveActively())
                .canCommunicate(dto.getCanCommunicate())
                .build();
    }

    public BusinessVerification toBusiness(BusinessRequestDTO dto) {
        return BusinessVerification.builder()
                .bizNumber(dto.getBizNumber())
                .companyName(dto.getCompanyName())
                .ownerName(dto.getOwnerName())
                .openingDate(dto.getOpeningDate())
                .build();
    }

    public PersonalRegistration toPersonal(PersonalRequestDTO dto) {
        return PersonalRegistration.builder()
                .name(dto.getName())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .contact(dto.getContact())
                .registrationPurpose(dto.getRegistrationPurpose())
                .address(dto.getAddress())
                .build();
    }
}
