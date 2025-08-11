package com.umc.tomorrow.domain.career.repository;

import com.umc.tomorrow.domain.career.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareerRepository  extends JpaRepository<Career, Long> {
    List<Career> findByResumeId(Long resumeId);
}
