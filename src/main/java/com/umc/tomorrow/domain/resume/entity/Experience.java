/**
 * 경력 정보 Entity
 * - 이력서 내 경력 정보
 * 작성자: (자동생성)
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.entity;

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
    private String duration;
    private int year;

    @ManyToOne(fetch = FetchType.LAZY)
    private Resume resume;

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }
} 