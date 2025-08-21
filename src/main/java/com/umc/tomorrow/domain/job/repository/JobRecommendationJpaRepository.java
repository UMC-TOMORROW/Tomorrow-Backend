package com.umc.tomorrow.domain.job.repository;

import com.umc.tomorrow.domain.job.entity.Job;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobRecommendationJpaRepository {

    private final EntityManager em;

    /**
     * 커서 공고의 score를 DB에서 직접 계산 (엔티티 로딩/LAZY 회피)
     */
    public Integer computeCursorScoreById(
            Long cursorId,
            boolean hasHuman,
            boolean hasDelivery,
            boolean hasPhysical,
            boolean hasSit,
            boolean hasStand
    ) {
        // 사용자가 true로 고른 항목만 더하는 경량 scoreExpr
        StringBuilder score = new StringBuilder();
        int termCount = 0;
        if (hasHuman)    { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canCommunicate   = true THEN 1 ELSE 0 END)"); }
        if (hasDelivery) { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canCarryObjects  = true THEN 1 ELSE 0 END)"); }
        if (hasPhysical) { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canMoveActively  = true THEN 1 ELSE 0 END)"); }
        if (hasSit)      { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canWorkSitting   = true THEN 1 ELSE 0 END)"); }
        if (hasStand)    { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canWorkStanding  = true THEN 1 ELSE 0 END)"); }

        if (termCount == 0) {
            // 사용자가 아무것도 선택 안 했다면 score는 의미 없음(호출측에서 이미 빈 결과 처리)
            return 0;
        }

        String jpql = "select " + score + " " +
                "from Job j join j.workEnvironment we " +
                "where j.id = :cursorId";

        Query q = em.createQuery(jpql);
        q.setParameter("cursorId", cursorId);
        Number n = (Number) q.getSingleResult();
        return (n != null) ? n.intValue() : 0;
    }

    public List<JobWithScore> findRecommendedByUser(
            Long userId, // 현재 시그니처 유지 (사용 안 해도 OK)
            boolean hasHuman,
            boolean hasDelivery,
            boolean hasPhysical,
            boolean hasSit,
            boolean hasStand,
            Integer cursorScore, // 첫 페이지면 null
            Long cursorId,       // 첫 페이지면 null
            int size
    ) {
        // 1) 사용자가 true로 고른 항목만 더하는 scoreExpr 구성
        StringBuilder score = new StringBuilder();
        int termCount = 0;
        if (hasHuman)    { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canCommunicate   = true THEN 1 ELSE 0 END)"); }
        if (hasDelivery) { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canCarryObjects  = true THEN 1 ELSE 0 END)"); }
        if (hasPhysical) { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canMoveActively  = true THEN 1 ELSE 0 END)"); }
        if (hasSit)      { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canWorkSitting   = true THEN 1 ELSE 0 END)"); }
        if (hasStand)    { if (termCount++ > 0) score.append("+"); score.append("(CASE WHEN we.canWorkStanding  = true THEN 1 ELSE 0 END)"); }

        if (termCount == 0) {
            // 호출측에서 이미 빈 결과 처리하겠지만 안전장치
            return List.of();
        }

        // 2) 사용자가 false로 고른 항목만 AND we.col = false 조건 추가 (OR 제거)
        List<String> negatives = new ArrayList<>(5);
        if (!hasHuman)    negatives.add("we.canCommunicate   = false");
        if (!hasDelivery) negatives.add("we.canCarryObjects  = false");
        if (!hasPhysical) negatives.add("we.canMoveActively  = false");
        if (!hasSit)      negatives.add("we.canWorkSitting   = false");
        if (!hasStand)    negatives.add("we.canWorkStanding  = false");

        StringBuilder jpql = new StringBuilder();
        jpql.append("select j, ").append(score).append(" as sc ")
                .append("from Job j join j.workEnvironment we ");

        // 2-1) 네거티브 필터
        boolean hasWhere = false;
        if (!negatives.isEmpty()) {
            jpql.append("where ");
            hasWhere = true;
            for (int i = 0; i < negatives.size(); i++) {
                if (i > 0) jpql.append(" and ");
                jpql.append(negatives.get(i));
            }
        }

        // 2-2) 최소 1개 이상 만족 (score >= 1) — scoreExpr이 0만 더할 수도 있으니 보장
        if (termCount > 0) {
            jpql.append(hasWhere ? " and " : " where ")
                    .append("(").append(score).append(") >= 1 ");
            hasWhere = true;
        }

        // 3) 키셋 페이징 (score DESC, id DESC)
        if (cursorScore != null && cursorId != null) {
            jpql.append(hasWhere ? " and (" : " where (")
                    .append("(").append(score).append(") < :cursorScore ")
                    .append("or (").append(score).append(") = :cursorScore and j.id < :cursorId)) ");
        }

        // 4) 정렬
        jpql.append(" order by ").append(score).append(" desc, j.id desc");

        Query q = em.createQuery(jpql.toString());
        if (cursorScore != null && cursorId != null) {
            q.setParameter("cursorScore", cursorScore);
            q.setParameter("cursorId", cursorId);
        }
        q.setMaxResults(size);
        q.setHint("org.hibernate.readOnly", true);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();

        List<JobWithScore> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Job job = (Job) row[0];
            Integer sc = ((Number) row[1]).intValue();
            result.add(new JobWithScore(job, sc));
        }
        return result;
    }

    @Getter
    @AllArgsConstructor
    public static class JobWithScore {
        private final Job job;
        private final Integer score;
    }
}
