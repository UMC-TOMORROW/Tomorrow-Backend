package com.umc.tomorrow.domain.searchAndFilter.repository;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.entity.WorkDays;
import com.umc.tomorrow.domain.searchAndFilter.dto.request.JobSearchRequestDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * - JPQL을 사용하여 검색 조건에 따라 Job 목록을 조회
 */
@Repository
public class JobSearchRepositoryImpl implements JobSearchRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 검색 조건에 맞는 Job 리스트를 조회
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 조건에 맞는 Job 리스트
     */
    @Override
    public List<Job> searchJobs(JobSearchRequestDTO dto) {
        // JPQL 동적 쿼리 생성 시작
        StringBuilder jpql = new StringBuilder("SELECT j FROM Job j WHERE j.isActive = true");

        // 제목 또는 설명에 키워드 포함 (제목만 포함일 경우 수정)
        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            jpql.append(" AND (j.title LIKE :keyword OR j.jobDescription LIKE :keyword)");
        }

        // 지역 키워드 조건
        if (dto.getLocationKeyword() != null && !dto.getLocationKeyword().isBlank()) {
            jpql.append(" AND j.location LIKE :locationKeyword");
        }

        // 시작 시간 조건
        if (dto.getTimeStart() != null && !dto.getTimeStart().isBlank()) {
            jpql.append(" AND FUNCTION('TIME', j.workStart) <= :timeStart");
        }

        // 종료 시간 조건
        if (dto.getTimeEnd() != null && !dto.getTimeEnd().isBlank()) {
            jpql.append(" AND FUNCTION('TIME', j.workEnd) >= :timeEnd");
        }

        // 직무 카테고리 조건
        if (dto.getJobCategories() != null && !dto.getJobCategories().isEmpty()) {
            jpql.append(" AND j.jobCategory IN :categories");
        }

        // 쿼리 생성
        TypedQuery<Job> query = em.createQuery(jpql.toString(), Job.class);

        // 동적으로 파라미터 설정
        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            query.setParameter("keyword", "%" + dto.getKeyword() + "%");
        }
        if (dto.getLocationKeyword() != null && !dto.getLocationKeyword().isBlank()) {
            query.setParameter("locationKeyword", "%" + dto.getLocationKeyword() + "%");
        }
        if (dto.getTimeStart() != null && !dto.getTimeStart().isBlank()) {
            query.setParameter("timeStart", LocalTime.parse(dto.getTimeStart()));
        }
        if (dto.getTimeEnd() != null && !dto.getTimeEnd().isBlank()) {
            query.setParameter("timeEnd", LocalTime.parse(dto.getTimeEnd()));
        }
        if (dto.getJobCategories() != null && !dto.getJobCategories().isEmpty()) {
            query.setParameter("categories", dto.getJobCategories());
        }

        // 기본 조건으로 필터링된 Job 리스트 조회
        List<Job> result = query.getResultList();

        // 요일 조건이 있는 경우, 메모리 내에서 필터링
        if (dto.getWorkDays() != null && !dto.getWorkDays().isEmpty()) {
            result = result.stream()
                    .filter(job -> {
                        WorkDays wd = job.getWorkDays();
                        return dto.getWorkDays().stream().anyMatch(day -> switch (day.toUpperCase()) {
                            case "MON" -> Boolean.TRUE.equals(wd.getMON());
                            case "TUE" -> Boolean.TRUE.equals(wd.getTUE());
                            case "WED" -> Boolean.TRUE.equals(wd.getWED());
                            case "THU" -> Boolean.TRUE.equals(wd.getTHU());
                            case "FRI" -> Boolean.TRUE.equals(wd.getFRI());
                            case "SAT" -> Boolean.TRUE.equals(wd.getSAT());
                            case "SUN" -> Boolean.TRUE.equals(wd.getSUN());
                            default -> false;
                        });
                    })
                    .collect(Collectors.toList());
        }

        return result;
    }
}
