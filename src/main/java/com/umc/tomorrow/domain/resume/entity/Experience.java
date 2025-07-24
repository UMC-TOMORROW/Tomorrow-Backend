/**
 * 경력 정보 Entity
 * - 이력서 내 경력 정보
 * 작성자: (자동생성)
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.entity;

import com.umc.tomorrow.domain.resume.enums.ExperienceDuration;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String place;
    private String task;

    @Enumerated(EnumType.STRING)
    private ExperienceDuration duration;

    private int year;
    private String description; // 상세 설명(선택)

    @ManyToOne(fetch = FetchType.LAZY)
    private Resume resume;

    public int getYear() {
        return year;
    }
} 