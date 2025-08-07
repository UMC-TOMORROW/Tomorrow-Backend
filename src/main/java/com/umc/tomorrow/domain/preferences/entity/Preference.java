/**
 * Preference Entity
 * - 사용자의 희망 조건(PreferenceType) 목록을 저장
 * 
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.entity;

import com.umc.tomorrow.domain.member.entity.User;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 희망 조건을 소유한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 희망 조건 목록 (EnumSet)
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<PreferenceType> preferences = new HashSet<>();
} 