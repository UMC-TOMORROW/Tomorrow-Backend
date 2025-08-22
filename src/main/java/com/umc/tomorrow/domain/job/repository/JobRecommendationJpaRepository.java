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

    /** ---- 유틸 ---- */
    private String buildScoreExpr(boolean hasHuman, boolean hasDelivery, boolean hasPhysical, boolean hasSit, boolean hasStand) {
        StringBuilder score = new StringBuilder();
        int termCount = 0;

        if (hasHuman)    { if (termCount++ > 0) score.append(" + "); score.append("(CASE WHEN we.canCommunicate   = true THEN 1 ELSE 0 END)"); }
        if (hasDelivery) { if (termCount++ > 0) score.append(" + "); score.append("(CASE WHEN we.canCarryObjects  = true THEN 1 ELSE 0 END)"); }
        if (hasPhysical) { if (termCount++ > 0) score.append(" + "); score.append("(CASE WHEN we.canMoveActively  = true THEN 1 ELSE 0 END)"); }
        if (hasSit)      { if (termCount++ > 0) score.append(" + "); score.append("(CASE WHEN we.canWorkSitting   = true THEN 1 ELSE 0 END)"); }
        if (hasStand)    { if (termCount++ > 0) score.append(" + "); score.append("(CASE WHEN we.canWorkStanding  = true THEN 1 ELSE 0 END)"); }

        return score.toString();
    }

    private List<String> buildNegatives(boolean hasHuman, boolean hasDelivery, boolean hasPhysical, boolean hasSit, boolean hasStand) {
        // 체크하지 않은 항목은 false 라고 가정(= NULL도 false로 본다)
        List<String> negatives = new ArrayList<>(5);
        if (!hasHuman)    negatives.add("coalesce(we.canCommunicate,  false) = false");
        if (!hasDelivery) negatives.add("coalesce(we.canCarryObjects, false) = false");
        if (!hasPhysical) negatives.add("coalesce(we.canMoveActively, false) = false");
        if (!hasSit)      negatives.add("coalesce(we.canWorkSitting,  false) = false");
        if (!hasStand)    negatives.add("coalesce(we.canWorkStanding, false) = false");
        return negatives;
    }

    private int countScoreTerms(boolean hasHuman, boolean hasDelivery, boolean hasPhysical, boolean hasSit, boolean hasStand) {
        int c = 0;
        if (hasHuman) c++;
        if (hasDelivery) c++;
        if (hasPhysical) c++;
        if (hasSit) c++;
        if (hasStand) c++;
        return c;
    }

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
        if (countScoreTerms(hasHuman, hasDelivery, hasPhysical, hasSit, hasStand) == 0) {
            return 0;
        }

        String scoreExpr = buildScoreExpr(hasHuman, hasDelivery, hasPhysical, hasSit, hasStand);

        String jpql = "select " + scoreExpr + " " +
                "from Job j left join j.workEnvironment we " +
                "where j.id = :cursorId";

        Query q = em.createQuery(jpql);
        q.setParameter("cursorId", cursorId);
        Number n = (Number) q.getSingleResult();
        return (n != null) ? n.intValue() : 0;
    }

    public List<JobWithScore> findRecommendedByUser(
            Long userId, // 현재 시그니처 유지
            boolean hasHuman,
            boolean hasDelivery,
            boolean hasPhysical,
            boolean hasSit,
            boolean hasStand,
            Integer cursorScore, // 첫 페이지면 null
            Long cursorId,       // 첫 페이지면 null
            int size
    ) {
        int termCount = countScoreTerms(hasHuman, hasDelivery, hasPhysical, hasSit, hasStand);
        if (termCount == 0) return List.of();

        String scoreExpr = buildScoreExpr(hasHuman, hasDelivery, hasPhysical, hasSit, hasStand);
        List<String> negatives = buildNegatives(hasHuman, hasDelivery, hasPhysical, hasSit, hasStand);

        StringBuilder jpql = new StringBuilder();
        jpql.append("select j, ").append(scoreExpr).append(" as sc ")
                .append("from Job j left join j.workEnvironment we ");

        boolean hasWhere = false;

        // 네거티브 필터(선택 안 한 항목은 false여야 함; NULL도 false 취급)
        if (!negatives.isEmpty()) {
            jpql.append("where ");
            hasWhere = true;
            for (int i = 0; i < negatives.size(); i++) {
                if (i > 0) jpql.append(" and ");
                jpql.append(negatives.get(i));
            }
        }

        // 최소 1개 이상 만족 (score >= 1)
        jpql.append(hasWhere ? " and " : " where ")
                .append("(").append(scoreExpr).append(") >= 1 ");
        hasWhere = true;

        // 키셋 페이징 (score DESC, id DESC)
        if (cursorScore != null && cursorId != null) {
            jpql.append(" and ( (").append(scoreExpr).append(") < :cursorScore ")
                    .append("or (").append(scoreExpr).append(") = :cursorScore and j.id < :cursorId ) ");
        }

        // 정렬
        jpql.append(" order by ").append(scoreExpr).append(" desc, j.id desc");

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
