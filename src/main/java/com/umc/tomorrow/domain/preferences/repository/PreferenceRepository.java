/**
 * PreferenceRepository
 * - Preference Entity에 대한 JPA 데이터 접근 레이어
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.repository;

import com.umc.tomorrow.domain.preferences.entity.Preference;
import com.umc.tomorrow.domain.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    /**
     * 사용자별 Preference 조회
     */
    Optional<Preference> findByUser(User user);
} 