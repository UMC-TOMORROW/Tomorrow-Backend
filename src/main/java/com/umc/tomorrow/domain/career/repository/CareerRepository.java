package com.umc.tomorrow.domain.career.repository;

import com.umc.tomorrow.domain.career.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository  extends JpaRepository<Career, Long> {
}
