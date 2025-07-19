package com.umc.tomorrow.domain.review.dto;

import com.umc.tomorrow.domain.review.dto.ReviewRequestDTO;
import com.umc.tomorrow.domain.review.entity.Review;
import com.umc.tomorrow.domain.member.entity.User;

public class ReviewConverter {
    public static Review toEntity(ReviewRequestDTO dto, User user) {
        return Review.builder()
                .postId(dto.getPostId())
                .stars(dto.getStars())
                .review(dto.getReview())
                .user(user)
                .build();
    }
}