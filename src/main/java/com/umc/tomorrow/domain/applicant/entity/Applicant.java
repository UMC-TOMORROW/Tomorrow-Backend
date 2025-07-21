/**
 * 지원자 Entity
 * - Job과 User를 연결하는 중간 테이블
 * - 지원 상태 관리 (합격/불합격)
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.applicant.entity;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applicant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Applicant extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    
    @Column(length = 500)
    private String message; // 지원 메시지
    
    private LocalDateTime appliedAt; // 지원일시
    
    private LocalDateTime statusUpdatedAt; // 상태 변경일시
    
    public enum ApplicationStatus {
        ACCEPTED("합격"),
        REJECTED("불합격");
        
        private final String description;
        
        ApplicationStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 지원 상태 업데이트
     */
    public void updateStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
        this.statusUpdatedAt = LocalDateTime.now();
    }
    
    /**
     * 지원 메시지 업데이트
     */
    public void updateMessage(String message) {
        this.message = message;
    }
} 