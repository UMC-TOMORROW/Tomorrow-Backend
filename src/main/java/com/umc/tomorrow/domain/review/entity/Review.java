/**
 * 후기(리뷰) entity
 *
 * 작성자: 정여진
 * 생성일: 2025-07-11
 */
package com.umc.tomorrow.domain.review.entity;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.entity.User;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{review.stars.notnull}")
    private int stars;

    @NotNull(message = "{review.review.notnull}")
    @Column(length = 100)
    private String review;

    @NotNull(message = "{review.user.notnull}")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "job_id")
    @JoinColumn(name = "post_id", nullable = false)
    private Job job;
}