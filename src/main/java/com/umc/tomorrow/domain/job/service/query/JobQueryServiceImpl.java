/**
 * 내 공고 조회 서비스 구현체
 * 작성자: 정여진
 * 생성일: 2025-07-25
 */
package com.umc.tomorrow.domain.job.service.query;

import com.umc.tomorrow.domain.job.converter.JobConverter;
import com.umc.tomorrow.domain.job.dto.request.MyPostResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.BusinessResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationListResponse;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationResponse;
import com.umc.tomorrow.domain.job.dto.response.JobDetailResponseDTO;
import com.umc.tomorrow.domain.job.entity.BusinessVerification;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
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
import com.umc.tomorrow.domain.review.repository.ReviewRepository.JobReviewCount;
import com.umc.tomorrow.global.common.exception.RestApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 🔎 로그
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobQueryServiceImpl implements JobQueryService {

    private final JobRepository jobRepository;
    private final JobConverter jobConverter;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PreferenceRepository preferenceRepository;
    private final JobRecommendationJpaRepository jobRecommendationJpaRepository;

    @Override
    public List<MyPostResponseDTO> getMyPosts(Long userId, String status) {
        final PostStatus postStatus;
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

    // 사업자 등록 정보 확인
    @Override
    public BusinessResponseDTO BusinessVerificationView(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        BusinessVerification business = user.getBusinessVerification();
        if (business == null) {
            throw new RestApiException(JobErrorStatus.BUSINESS_NOT_FOUND);
        }

        return jobConverter.toBusinessResponseDTO(business);
    }

    // 내일 추천
    @Override
    @Transactional(readOnly = true)
    public GetRecommendationListResponse getTomorrowRecommendations(Long userId, Long cursorJobId, int size) {

        if (log.isDebugEnabled()) {
            log.debug("[REC] request userId={} cursorJobId={} size={}", userId, cursorJobId, size);
        }

        Preference pref = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new PreferenceException(PreferenceErrorStatus.PREFERENCE_NOT_FOUND));

        // 사용자가 true로 고른 항목들
        Set<PreferenceType> positives = pref.getPreferences();
        boolean hasHuman    = positives.contains(PreferenceType.HUMAN);
        boolean hasDelivery = positives.contains(PreferenceType.DELIVERY);
        boolean hasPhysical = positives.contains(PreferenceType.PHYSICAL);
        boolean hasSit      = positives.contains(PreferenceType.SIT);
        boolean hasStand    = positives.contains(PreferenceType.STAND);

        if (log.isDebugEnabled()) {
            log.debug("[REC] positives(size={}): {}", positives.size(), positives);
            log.debug("[REC] flags H={} D={} P={} SIT={} STAND={}",
                    hasHuman, hasDelivery, hasPhysical, hasSit, hasStand);
        }

        // 전부 false면 추천 X
        if (!(hasHuman || hasDelivery || hasPhysical || hasSit || hasStand)) {
            if (log.isDebugEnabled()) log.debug("[REC] no positive flags → return empty");
            return GetRecommendationListResponse.builder()
                    .recommendationList(List.of())
                    .hasNext(false)
                    .build();
        }

        // 1) 커서 점수 DB에서 계산
        Integer cursorScore = null;
        Long cursorIdForQuery = cursorJobId;

        if (cursorJobId != null) {
            long t0 = System.currentTimeMillis();
            cursorScore = jobRecommendationJpaRepository.computeCursorScoreById(
                    cursorJobId, hasHuman, hasDelivery, hasPhysical, hasSit, hasStand
            );
            long t1 = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
                log.debug("[REC] computed cursorScore={} for cursorJobId={} ({} ms)",
                        cursorScore, cursorJobId, (t1 - t0));
            }
            // 0점(또는 null) 커서는 키셋 모순을 만들므로 무시
            if (cursorScore == null || cursorScore <= 0) {
                if (log.isDebugEnabled()) {
                    log.debug("[REC] cursorScore <= 0 → ignore cursor (skip keyset condition)");
                }
                cursorScore = null;
                cursorIdForQuery = null;
            }
        } else {
            if (log.isDebugEnabled()) log.debug("[REC] first page (no cursor)");
        }

        // 2) 추천 조회 (+1 페치로 hasNext 계산)
        long t2 = System.currentTimeMillis();
        var rows = jobRecommendationJpaRepository.findRecommendedByUser(
                userId,
                hasHuman, hasDelivery, hasPhysical, hasSit, hasStand,
                cursorScore, cursorIdForQuery,
                size + 1
        );
        long t3 = System.currentTimeMillis();

        if (log.isDebugEnabled()) {
            log.debug("[REC] repo rows={} ({} ms) cursorApplied={} cursorScore={} cursorId={}",
                    rows.size(), (t3 - t2), (cursorScore != null && cursorIdForQuery != null),
                    cursorScore, cursorIdForQuery);
            rows.stream().limit(10).forEach(jws ->
                    log.debug("[REC] row jobId={} score={}", jws.getJob().getId(), jws.getScore()));
        }

        boolean hasNext = rows.size() > size;
        var page = hasNext ? rows.subList(0, size) : rows;

        if (log.isDebugEnabled()) {
            log.debug("[REC] page.size={} hasNext={}", page.size(), hasNext);
        }

        // 3) 리뷰 카운트 일괄 조회 (N+1 제거) - page 기준
        List<Long> jobIds = page.stream().map(jws -> jws.getJob().getId()).toList();
        if (log.isDebugEnabled()) {
            log.debug("[REC] review count for jobIds(size={}): {}", jobIds.size(), jobIds);
        }

        Map<Long, Long> countMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            long t4 = System.currentTimeMillis();
            List<JobReviewCount> reviewRows = reviewRepository.countByJobIdInGroupByJob(jobIds);
            long t5 = System.currentTimeMillis();

            for (JobReviewCount r : reviewRows) {
                countMap.put(r.getJobId(), r.getCnt());
            }
            if (log.isDebugEnabled()) {
                log.debug("[REC] review counts fetched {} rows in {} ms", reviewRows.size(), (t5 - t4));
            }
        }

        // 4) 변환 (page 기준)
        List<GetRecommendationResponse> responseList = page.stream()
                .map(jws -> jobConverter.toRecommendationResponse(
                        jws.getJob(),
                        countMap.getOrDefault(jws.getJob().getId(), 0L)
                ))
                .toList();

        return GetRecommendationListResponse.builder()
                .recommendationList(responseList)
                .hasNext(hasNext)
                .build();
    }
}
