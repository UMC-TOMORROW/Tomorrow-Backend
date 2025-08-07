package com.umc.tomorrow.domain.job.converter;

import com.umc.tomorrow.domain.job.dto.request.*;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationResponse;
import com.umc.tomorrow.domain.job.dto.response.JobDetailResponseDTO;
import com.umc.tomorrow.domain.job.entity.*;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import com.umc.tomorrow.domain.preferences.entity.Preference;
import org.springframework.stereotype.Component;
import com.umc.tomorrow.domain.preferences.entity.PreferenceType;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
                .status(PostStatus.OPEN)
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
                .jobCategory(dto.getJobCategory())
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

    public MyPostResponseDTO toMyPostResponseDto(Job job) {
        return MyPostResponseDTO.builder()
                .postId(job.getId())
                .title(job.getTitle())
                .status(job.getStatus().getDisplayValue())
                .date(job.getDeadline().toLocalDate())
                .location(job.getLocation())
                .tags(List.of(
                        job.getJobCategory().getDescription()
                        // 추후 WorkEnvironment 등에서 태그 추가 가능
                ))
                .build();
    }

    public JobDetailResponseDTO toJobDetailResponseDTO(Job job) {

        Set<PreferenceType> preferences = Optional.ofNullable(job.getUser().getPreference())
                .map(Preference::getPreferences)
                .orElse(Collections.emptySet());

        return JobDetailResponseDTO.builder()
                .title(job.getTitle())
                .jobDescription(job.getJobDescription())
                .workPeriod(job.getWorkPeriod())
                .isPeriodNegotiable(job.getIsPeriodNegotiable())
                .workStart(job.getWorkStart())
                .workEnd(job.getWorkEnd())
                .isTimeNegotiable(job.getIsTimeNegotiable())
                .paymentType(job.getPaymentType())
                .jobCategory(job.getJobCategory())
                .salary(job.getSalary())
                .jobImageUrl(job.getJobImageUrl())
                .companyName(job.getUser().getName())
                .isActive(job.getIsActive())
                .recruitmentLimit(job.getRecruitmentLimit())
                .deadline(job.getDeadline())
                .preferredQualifications(job.getPreferredQualifications())
                .location(job.getLocation())
                .alwaysHiring(job.getAlwaysHiring())
                .workDays(toWorkDaysRequestDTO(job.getWorkDays()))
                .workEnvironment(toWorkEnvironmentRequestDTO(job.getWorkEnvironment()))
                .preferences(preferences)
                .build();
    }

    public WorkDaysRequestDTO toWorkDaysRequestDTO(WorkDays workDays) {
        return WorkDaysRequestDTO.builder()
                .MON(workDays.getMON())
                .TUE(workDays.getTUE())
                .WED(workDays.getWED())
                .THU(workDays.getTHU())
                .FRI(workDays.getFRI())
                .SAT(workDays.getSAT())
                .SUN(workDays.getSUN())
                .isDayNegotiable(workDays.getIsDayNegotiable())
                .build();
    }

    public WorkEnvironmentRequestDTO toWorkEnvironmentRequestDTO(WorkEnvironment workEnvironment) {
        return WorkEnvironmentRequestDTO.builder()
                .canWorkSitting(workEnvironment.isCanWorkSitting())
                .canWorkStanding(workEnvironment.isCanWorkStanding())
                .canLiftHeavyObjects(workEnvironment.isCanLiftHeavyObjects())
                .canLiftLightObjects(workEnvironment.isCanLiftLightObjects())
                .canMoveActively(workEnvironment.isCanMoveActively())
                .canCommunicate(workEnvironment.isCanCommunicate())
                .build();
    }

    public GetRecommendationResponse toRecommendationResponse(Job job, long reviewCount) {
        return GetRecommendationResponse.builder()
                .id(job.getId())
                .companyName(job.getCompanyName())
                .title(job.getTitle())
                .location(job.getLocation())
                .salary(job.getSalary())
                .paymentType(job.getPaymentType())
                .isTimeNegotiable(job.getIsTimeNegotiable())
                .workStart(job.getIsTimeNegotiable() ? null : job.getWorkStart())
                .workEnd(job.getIsTimeNegotiable() ? null : job.getWorkEnd())
                .isPeriodNegotiable(job.getIsPeriodNegotiable())
                .workPeriod(job.getIsPeriodNegotiable() ? null : job.getWorkPeriod())
                .reviewCount(reviewCount)
                .build();
    }
}
