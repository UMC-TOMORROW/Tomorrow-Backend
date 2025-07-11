package com.umc.tomorrow.domain.careertalk.repository;

import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareertalkRepository extends JpaRepository<Careertalk, Long> {
    Slice<Careertalk> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
