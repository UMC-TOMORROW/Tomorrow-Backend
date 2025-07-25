package com.umc.tomorrow.domain.introduction.repository;

import com.umc.tomorrow.domain.introduction.entity.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroductionRepository extends JpaRepository<Introduction, Long> {
}
