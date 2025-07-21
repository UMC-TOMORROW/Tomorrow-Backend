/**
 * 지원서 Entity
 * - User와 Job을 연결하는 지원서 정보
 * - 합격/불합격 상태 관리
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.entity;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.review.entity.Review;
import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Application extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 100, nullable = false)
    private String content; // 지원정보 입력란
    
    @Column(nullable = true)
    private Boolean status; // 합격여부 (true: 합격, false: 불합격, null: 미정)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 합격/불합격 상태 업데이트
     */
    public void updateStatus(Boolean newStatus) {
        this.status = newStatus;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * 지원 내용 업데이트
     */
    public void updateContent(String content) {
        this.content = content;
        this.setUpdatedAt(LocalDateTime.now());
    }

    protected void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 