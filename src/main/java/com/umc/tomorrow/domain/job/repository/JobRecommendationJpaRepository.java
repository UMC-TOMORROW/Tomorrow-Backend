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

    public List<JobWithScore> findRecommendedByUser(
            Long userId,
            boolean hasHuman,
            boolean hasDelivery,
            boolean hasPhysical,
            boolean hasSit,
            boolean hasStand,
            Integer cursorScore, // 첫 페이지면 null
            Long cursorId,       // 첫 페이지면 null
            int size
    ) {
        // 점수: 유저가 true로 고른 항목에 한해, env가 true면 +1
        final String scoreExpr =
                "(CASE WHEN :hasHuman    = true AND we.canCommunicate   = true THEN 1 ELSE 0 END)"
                        + "+(CASE WHEN :hasDelivery = true AND we.canCarryObjects  = true THEN 1 ELSE 0 END)"
                        + "+(CASE WHEN :hasPhysical = true AND we.canMoveActively  = true THEN 1 ELSE 0 END)"
                        + "+(CASE WHEN :hasSit      = true AND we.canWorkSitting   = true THEN 1 ELSE 0 END)"
                        + "+(CASE WHEN :hasStand    = true AND we.canWorkStanding  = true THEN 1 ELSE 0 END)";

        // 네거티브: 유저가 false로 고른 항목은 env가 반드시 false
        // → (env=false) OR (그 항목을 유저가 선택함)
        final String negatives =
                "("
                        + " (we.canCommunicate   = false OR :hasHuman    = true) AND"
                        + " (we.canCarryObjects  = false OR :hasDelivery = true) AND"
                        + " (we.canMoveActively  = false OR :hasPhysical = true) AND"
                        + " (we.canWorkSitting   = false OR :hasSit      = true) AND"
                        + " (we.canWorkStanding  = false OR :hasStand    = true)"
                        + ")";

        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT j, ").append(scoreExpr).append(" AS score ")
                .append("FROM Job j ")
                .append("JOIN j.workEnvironment we ")
                .append("WHERE ").append(negatives)          // 1) 원치 않는 조건 제거
                .append(" AND (").append(scoreExpr).append(" >= 1) "); // 2) true 중 1개 이상 만족

        // 3) 키셋 페이징 (score DESC, id DESC)
        if (cursorScore != null && cursorId != null) {
            jpql.append(" AND (")
                    .append(scoreExpr).append(" < :cursorScore ")
                    .append("OR (").append(scoreExpr).append(" = :cursorScore AND j.id < :cursorId)) ");
        }

        jpql.append(" ORDER BY ").append(scoreExpr).append(" DESC, j.id DESC");

        Query q = em.createQuery(jpql.toString());
        q.setParameter("hasHuman",    hasHuman);
        q.setParameter("hasDelivery", hasDelivery);
        q.setParameter("hasPhysical", hasPhysical);
        q.setParameter("hasSit",      hasSit);
        q.setParameter("hasStand",    hasStand);

        if (cursorScore != null && cursorId != null) {
            q.setParameter("cursorScore", cursorScore);
            q.setParameter("cursorId", cursorId);
        }
        q.setMaxResults(size);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();

        List<JobWithScore> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Job job = (Job) row[0];
            Integer score = ((Number) row[1]).intValue();
            result.add(new JobWithScore(job, score));
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

