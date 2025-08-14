/**
 * 찜 엔티티
 * 작성자: 정여진
 * 작성일: 2025-08-05
 * job_bookmark db와 연결됨
 */
package com.umc.tomorrow.domain.jobbookmark.entity;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobBookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}
