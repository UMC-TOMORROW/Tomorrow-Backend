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
import org.springframework.stereotype.Repository;

@Repository
public interface CareertalkRepository extends JpaRepository<Careertalk, Long> {

    Slice<Careertalk> findAllByOrderByIdDesc(Pageable pageable);
    Slice<Careertalk> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);

    Slice<Careertalk> findByTitleContainingIgnoreCaseOrderByIdDesc(String title, Pageable pageable);
    Slice<Careertalk> findByTitleContainingIgnoreCaseAndIdLessThanOrderByIdDesc(String title, Long id, Pageable pageable);

    Slice<Careertalk> findByCategoryOrderByIdDesc(String category, Pageable pageable);
    Slice<Careertalk> findByCategoryAndIdLessThanOrderByIdDesc(String category, Long id, Pageable pageable);
}
