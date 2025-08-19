/**
 * JobBookmarkRepository
 * - JobBookmark 엔티티에 대한 JPA Repository
 * - 사용자 ID를 기반으로 찜한 공고를 조회할 수 있는 메서드 포함
 *
 * 작성자: 정여진
 * 작성일: 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.repository;

import com.umc.tomorrow.domain.jobbookmark.entity.JobBookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobBookmarkRepository extends JpaRepository<JobBookmark, Long> {
    Optional<JobBookmark> findByUserIdAndJobId(Long userId, Long jobId);

    // 특정 사용자와 직업 ID로 북마크가 있는지 확인
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
    /**
     * 특정 사용자에 대한 JobBookmark 목록을 커서 기반 페이지네이션을 사용하여 슬라이스로 조회
     * @param userId 사용자의 ID.
     * @param cursor 이전 페이지의 마지막 항목 ID (첫 페이지의 경우 null).
     * @param pageable 페이지네이션 정보.
     * @return JobBookmark 슬라이스.
     */
    Slice<JobBookmark> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long cursor, Pageable pageable);

    /**
     * 특정 사용자에 대한 JobBookmark 목록을 ID 내림차순으로 정렬하여 페이지네이션으로 조회
     * @param userId 사용자의 ID.
     * @param pageable 페이지네이션 정보.
     * @return JobBookmark 슬라이스.
     */
    Slice<JobBookmark> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}