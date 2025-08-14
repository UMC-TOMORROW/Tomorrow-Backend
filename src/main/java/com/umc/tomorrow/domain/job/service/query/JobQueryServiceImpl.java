/**
 * 내 공고 조회 서비스 구현체
 * 작성자: 정인도
 * 생성일: 2025-07-25
 */
package com.umc.tomorrow.domain.job.service.query;

import com.umc.tomorrow.domain.job.converter.JobConverter;
import com.umc.tomorrow.domain.job.dto.request.MyPostResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationListResponse;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationResponse;
import com.umc.tomorrow.domain.job.dto.response.JobDetailResponseDTO;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.entity.WorkEnvironment;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.repository.JobRecommendationJpaRepository;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.exception.MemberException;
import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.preferences.entity.Preference;
import com.umc.tomorrow.domain.preferences.entity.PreferenceType;
import com.umc.tomorrow.domain.preferences.exception.PreferenceException;
import com.umc.tomorrow.domain.preferences.exception.code.PreferenceErrorStatus;
import com.umc.tomorrow.domain.preferences.repository.PreferenceRepository;
import com.umc.tomorrow.domain.review.repository.ReviewRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobQueryServiceImpl implements JobQueryService {

    private final JobRepository jobRepository;
    private final JobConverter jobConverter;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PreferenceRepository preferenceRepository;
    private final JobRecommendationJpaRepository jobRecommendationJpaRepository;

    @Override
    public List<MyPostResponseDTO> getMyPosts(Long userId, String status) {
        PostStatus postStatus;

        try {
            postStatus = PostStatus.from(status);
        } catch (IllegalArgumentException e) {
            throw new JobException(JobErrorStatus.POST_STATUS_INVALID);
        }

        List<Job> jobs = jobRepository.findByUserIdAndStatus(userId, postStatus);

        return jobs.stream()
                .map(jobConverter::toMyPostResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobDetailResponseDTO getJobDetail(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));

        return jobConverter.toJobDetailResponseDTO(job);
    }

    // 내일 추천
    @Override
    public GetRecommendationListResponse getTomorrowRecommendations(Long userId, Long cursorJobId, int size) {

        // 1) 유저 & 선호도 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        Preference pref = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new PreferenceException(PreferenceErrorStatus.PREFERENCE_NOT_FOUND));

        Set<PreferenceType> positives = pref.getPreferences(); // 유저가 true로 고른 항목들
        boolean hasHuman    = positives.contains(PreferenceType.HUMAN);
        boolean hasDelivery = positives.contains(PreferenceType.DELIVERY);
        boolean hasPhysical = positives.contains(PreferenceType.PHYSICAL);
        boolean hasSit      = positives.contains(PreferenceType.SIT);
        boolean hasStand    = positives.contains(PreferenceType.STAND);

        // 전부 false면 추천 x
        if (!(hasHuman || hasDelivery || hasPhysical || hasSit || hasStand)) {
            return GetRecommendationListResponse.builder()
                    .recommendationList(List.of())
                    .hasNext(false)
                    .build();
        }

        // 2) 커서 점수 계산: 기존 API는 cursorJobId만 주니까, 그 공고의 score를 동일 기준으로 계산
        Integer cursorScore = null;
        if (cursorJobId != null) {
            Job cursorJob = jobRepository.findById(cursorJobId)
                    .orElseThrow(() -> new JobException(JobErrorStatus.JOB_NOT_FOUND));

            if (cursorJob != null && cursorJob.getWorkEnvironment() != null) {
                cursorScore = computeCursorScore(cursorJob.getWorkEnvironment(), hasHuman, hasDelivery, hasPhysical, hasSit, hasStand);
            }
        }

        // 3) DB에서 바로 필터/스코어/정렬/키셋 처리
        var page = jobRecommendationJpaRepository.findRecommendedByUser(
                userId,
                hasHuman, hasDelivery, hasPhysical, hasSit, hasStand,
                cursorScore, cursorJobId,
                size
        );

        // 4) 결과 변환
        List<GetRecommendationResponse> responseList = page.stream()
                .map(j -> jobConverter.toRecommendationResponse(
                        j.getJob(), reviewRepository.countByJobId(j.getJob().getId())))
                .toList();

        return GetRecommendationListResponse.builder()
                .recommendationList(responseList)
                .hasNext(page.size() == size)
                .build();
    }

    /**
     * JPQL의 scoreExpr과 동일한 기준으로 커서 공고의 점수를 자바에서 계산
     */
    private int computeCursorScore(WorkEnvironment env, boolean hasHuman, boolean hasDelivery, boolean hasPhysical, boolean hasSit, boolean hasStand) {
        int score = 0;
        if (hasHuman    && env.isCanCommunicate())  score++;
        if (hasDelivery && env.isCanCarryObjects()) score++;
        if (hasPhysical && env.isCanMoveActively()) score++;
        if (hasSit      && env.isCanWorkSitting())  score++;
        if (hasStand    && env.isCanWorkStanding()) score++;
        return score;
    }
}
