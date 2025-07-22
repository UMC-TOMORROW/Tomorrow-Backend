/**
 * CareertalkRepository
 * - 커리어톡 엔티티 데이터 접근 레이어
 * 작성자: 이승주
 * 생성일: 2025-07-10
 * 수정일: 20205-07-20
 */
package com.umc.tomorrow.domain.careertalk.repository;

import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareertalkRepository extends JpaRepository<Careertalk, Long> {
    // 첫 요청 (cursor 없음)
    Slice<Careertalk> findAllByOrderByIdDesc(Pageable pageable);

    // 커서 기반 요청
    Slice<Careertalk> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);
}
