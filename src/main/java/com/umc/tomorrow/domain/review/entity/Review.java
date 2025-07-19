/**
 * 후기(리뷰) entity
 *
 * 작성자: 정여진
 * 생성일: 2025-07-11
 */
package com.umc.tomorrow.domain.review.entity;

import com.umc.tomorrow.domain.member.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private int stars;

    @Column(length = 100)
    private String review;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}